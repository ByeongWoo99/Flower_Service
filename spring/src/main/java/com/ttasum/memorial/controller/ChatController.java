package com.ttasum.memorial.controller;

import com.ttasum.memorial.dto.chat.ChatApiResponse;
import com.ttasum.memorial.dto.chat.ChatDto;
import com.ttasum.memorial.service.chat.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    /**
     * @param inputMsg 사용자 입력 메시지 DTO
     * @return {@code Mono<ResponseEntity<ChatApiResponse>>} 챗봇 응답 DTO
     * @apiNote LLM 모델을 사용하여 챗봇 응답을 받는 API 엔드포인트
     */
    @PostMapping("/chat")
    public Mono<ResponseEntity<ChatApiResponse>> getChatResponse(@RequestBody ChatDto inputMsg) {
        // 입력 유효성 검사: 빈 문자열 여부 확인
        String question = inputMsg.getQuestion();
        if (question == null || question.trim().isEmpty()) {
            return getValidationCheckError(question);
        }
        log.info("[INPUT_CHECK_OK] 입력 유효성 검사 성공: \"{}\"", question);
        return chatService.getLLMResponse(inputMsg);
    }

    private static Mono<ResponseEntity<ChatApiResponse>> getValidationCheckError(String question) {
        log.warn("[INPUT_CHECK_FAILED] 사용자 입력 유효성 검사 실패: \"{}\"", question);
        return Mono.just(
                ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(ChatApiResponse.error(HttpStatus.BAD_REQUEST.value(), "빈 공백만 입력할 수 없습니다. 좀 더 구체적으로 입력해 주세요."))
        );
    }
}
