package com.ttasum.memorial.exception.blameText;

public class BlameTextException extends RuntimeException {
    public BlameTextException(String message) {
        super(message);
    }
    public BlameTextException() {
        super("게시글을 찾을 수 없음");
    }
}
