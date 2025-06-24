package com.ttasum.memorial.exception.common.serverError;

//파일 저장 중 예외 발생
public class FileStorageException extends RuntimeException {
    public FileStorageException() {
        super("파일 저장 중 오류가 발생했습니다.");
    }
}
