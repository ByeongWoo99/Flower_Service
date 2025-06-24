package com.ttasum.memorial.exception.common.badRequest;

public class InvalidCommentPasscodeException extends BadRequestException {
    public InvalidCommentPasscodeException(Integer commentSeq) {
        super("댓글 번호 [" + commentSeq + "]의 비밀번호가 일치하지 않습니다.");
    }
}
