package com.ttasum.memorial.exception.common.badRequest;

public class InvalidPaginationParameterException extends BadRequestException {
    public InvalidPaginationParameterException (String message) {
        super(message);
    }
}
