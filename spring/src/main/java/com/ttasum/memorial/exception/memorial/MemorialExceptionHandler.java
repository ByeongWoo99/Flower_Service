package com.ttasum.memorial.exception.memorial;

import com.ttasum.memorial.dto.common.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.ttasum.memorial.controller.memorial")
@Slf4j
public class MemorialExceptionHandler {

    @ExceptionHandler(MemorialNotFoundException.class)
    public ResponseEntity<ApiResponse> handleMemorialNotFound(MemorialNotFoundException ex) {
        log.info("Memorial 예외 발생: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.fail(HttpStatus.NOT_FOUND.value(), ex.getMessage()));
    }
}
