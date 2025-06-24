package com.ttasum.memorial.exception.donationStory;

public class InvalidCommentPasscodeException extends RuntimeException {
    public InvalidCommentPasscodeException(Integer commentSeq) {
        super("댓글 번호 [" + commentSeq + "]의 비밀번호가 일치하지 않습니다.");
    }
}
