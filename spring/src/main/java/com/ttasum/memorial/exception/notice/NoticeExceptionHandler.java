package com.ttasum.memorial.exception.notice;

import com.ttasum.memorial.dto.common.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.ttasum.memorial.controller.notice")
@Slf4j
public class NoticeExceptionHandler {

    @ExceptionHandler(NoticeNotFoundException.class)
    public ResponseEntity<ApiResponse> handleNoticeNotFound(NoticeNotFoundException ex) {
        log.info("Notice 예외 발생: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.fail(HttpStatus.NOT_FOUND.value(), ex.getMessage()));
    }
}
