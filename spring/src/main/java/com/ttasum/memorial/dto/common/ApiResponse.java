package com.ttasum.memorial.dto.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class ApiResponse {
    private boolean success;  // 처리 결과
    private int code;     // HTTP 상태 코드
    private String message;  // 사용자 메시지

    // 성공 응답
    public static ApiResponse ok(int code, String message) {
        return new ApiResponse(true, code, message);
    }

    // 실패 응답 (공통)
    public static ApiResponse fail(int code, String message) {
        return new ApiResponse(false, code, message);
    }

    // 400 Bad Request 전용
    public static ApiResponse badRequest(String message) {
        return fail(HttpStatus.BAD_REQUEST.value(), message);
    }

    // 404 Not Found 전용
    public static ApiResponse notFound(String message) {
        return fail(HttpStatus.NOT_FOUND.value(), message);
    }


    // 409 Conflict 전용
    public static ApiResponse conflict(String message) {
        return fail(HttpStatus.CONFLICT.value(), message);
    }

    // 500 Internal Server Error 전용
    public static ApiResponse serverError() {
        return fail(HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버 내부 오류가 발생했습니다.");
    }
}
