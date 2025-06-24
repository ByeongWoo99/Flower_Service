package com.ttasum.memorial.exception.common.badRequest;

// 비밀번호 인증 실패 시 예외 발생(400)
public class InvalidPasscodeException extends BadRequestException {
    public InvalidPasscodeException() {
        super("비밀번호가 일치하지 않습니다.");
    }
}
