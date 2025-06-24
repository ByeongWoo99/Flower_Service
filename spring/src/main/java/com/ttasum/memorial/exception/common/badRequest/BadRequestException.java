package com.ttasum.memorial.exception.common.badRequest;

// 상위 400 예외
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
