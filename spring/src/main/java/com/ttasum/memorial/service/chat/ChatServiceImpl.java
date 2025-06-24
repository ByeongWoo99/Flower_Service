package com.ttasum.memorial.service.chat;

import com.ttasum.memorial.dto.chat.ChatApiResponse;
import com.ttasum.memorial.dto.chat.ChatDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class ChatServiceImpl implements ChatService {

    private final WebClient webClient;

    public ChatServiceImpl(@Qualifier("chat")WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * 챗봇 응답을 처리하는 핵심 비즈니스 로직.
     * 비동기 논블로킹 방식({@link WebClient})을 통해 FastAPI로부터 응답을 받아 처리합니다.
     *
     * @param inputMsg 사용자 입력 메시지 DTO
     * @return {@code Mono<ResponseEntity<ChatApiResponse>>} 챗봇 응답 DTO
     */
    @Override
    public Mono<ResponseEntity<ChatApiResponse>> getLLMResponse(ChatDto inputMsg) {
        log.info("[LLM_API_INIT] 파이썬 챗봇 API 호출 시작: \"{}\"", inputMsg.getQuestion());
        return webClient.post()
                .uri("/chat/")
                .bodyValue(inputMsg)
                .exchangeToMono(ChatServiceImpl::getCustomResponseFromPython)
                .doOnSuccess(response -> log.info("[LLM_API_RECEIVED] 파이썬 챗봇 API 응답 수신: {}", response.getStatusCode()))
                .onErrorResume(ChatServiceImpl::getDefaultErrorResponse);
    }

    private static Mono<ResponseEntity<ChatApiResponse>> getDefaultErrorResponse(Throwable e) {
        // 그 외 Exception (WebClientRequestException 등) 처리
        log.error("[WEBCLIENT_COMM_ERROR] 파이썬 챗봇 API 통신 중 에러 발생: {}", e.getMessage(), e);
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(ChatApiResponse.error()));
    }

    private static Mono<ResponseEntity<ChatApiResponse>> getCustomResponseFromPython(ClientResponse response) {
        // 정상적 흐름일 경우: 200 OK + 본문 내용 반환
        if (response.statusCode().is2xxSuccessful()) {
            return getSuccessResponse(response);
        }
        // 400, 500 (WebClientResponseException) 발생한 경우: 상태 코드 + 오류 내용 반환
        return getErrorResponse(response);
    }

    private static Mono<ResponseEntity<ChatApiResponse>> getErrorResponse(ClientResponse response) {
        return response.bodyToMono(ChatApiResponse.class)
                .map(result -> {
                            log.error("[PY_ERR_BODY_PARSING] 파이썬 챗봇 API 에러 응답 본문 파싱 성공");
                            return ResponseEntity.status(response.statusCode())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .body(result);
                        }
                );
    }

    private static Mono<ResponseEntity<ChatApiResponse>> getSuccessResponse(ClientResponse response) {
        return response.bodyToMono(ChatApiResponse.class)
                .map(result -> {
                    log.info("[PY_OK_BODY_PARSING] 파이썬 챗봇 API 성공 응답 본문 파싱 성공");
                    return ResponseEntity.status(HttpStatus.OK)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(result);
                });
    }
}