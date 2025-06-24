package com.ttasum.memorial.exception.forbiddenWord;

import lombok.Getter;

@Getter
public class ForbiddenWordException extends RuntimeException{

    public ForbiddenWordException(String message) {
        super(message);
    }

}
