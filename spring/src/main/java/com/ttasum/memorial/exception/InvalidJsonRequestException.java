package com.ttasum.memorial.exception;

public class InvalidJsonRequestException extends RuntimeException {
    public InvalidJsonRequestException(String message) {
        super(message);
    }
}
