package com.ttasum.memorial.exception.blameText;

public class MissingSentenceException extends RuntimeException {
    public MissingSentenceException(String message) {
        super(message);
    }
    public MissingSentenceException() {
        super("Detail 문장을 찾을 수 없습니다.");
    }
}
