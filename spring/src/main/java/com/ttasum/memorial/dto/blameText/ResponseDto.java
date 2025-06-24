package com.ttasum.memorial.dto.blameText;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

// factory 패턴
// 사용자에게 전달할 응답 dto
@Getter
@Builder
public class ResponseDto {
    private boolean success;
    private String message;
    private String noticeText;
    private int code;

    public static ResponseDto badRequest(String message) {
        return ResponseDto.builder()
                .success(false)
                .code(HttpStatus.BAD_REQUEST.value())
                .message(message)
                .build();
    }

    public static ResponseDto ok(String noticeText) {
        return ResponseDto.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message("성공적으로 등록되었습니다.")
                .noticeText(noticeText)
                .build();
    }
}
