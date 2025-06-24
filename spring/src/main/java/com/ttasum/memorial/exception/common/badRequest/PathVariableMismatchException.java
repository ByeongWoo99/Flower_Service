package com.ttasum.memorial.exception.common.badRequest;

//경로와 본문 값 불일치 (400 Bad Request)
public class PathVariableMismatchException extends BadRequestException {
    public PathVariableMismatchException(String message) {
        super(message);
    }
}
