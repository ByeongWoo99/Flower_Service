package com.ttasum.memorial.dto.chat;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;

import java.util.Map;

/**
 * 챗봇 응답에 사용할 DTO
 */
@Getter
@ToString
@Builder
public class ChatApiResponse {
    private final boolean success; // LLM 메시지 반환 여부
    private final int code;        // 상태 코드
    private final String message;  // 성공 시 응답 결과 메시지 및 실패 시 사용자에게 표시할 오류 메시지
    @Nullable
    private final Map<String, String> data; // 챗봇 실제 응답 메시지

    public static final String ERR_DEFAULT_MSG = "내부 네트워크 서버 오류, 타임 아웃 등으로 일시적인 문제가 발생했습니다. 잠시 후 다시 이용해 주세요.";

    @JsonCreator
    public ChatApiResponse(
            @JsonProperty("success") boolean success,
            @JsonProperty("code") int code,
            @JsonProperty("message") String message,
            @JsonProperty("data") @Nullable Map<String, String> data) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // 스프링 입력 유효성 검사, 파이썬에서 발생한 에러 (지정 상태코드, 지정 메시지)
    public static ChatApiResponse error(int code, String message) {
        return ChatApiResponse.builder()
                .success(false)
                .code(code)
                .message(message)
                .data(null)
                .build();
    }

    // 그 외 발생한 에러 (기본 500, 기본 메시지)
    public static ChatApiResponse error() {
        return ChatApiResponse.builder()
                .success(false)
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message(ERR_DEFAULT_MSG)
                .data(null)
                .build();
    }
}
