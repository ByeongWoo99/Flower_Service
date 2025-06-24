package com.ttasum.memorial.exception.heavenLetter;

import com.ttasum.memorial.exception.common.notFound.NotFoundException;

public class HeavenLetterCommentNotFoundException extends NotFoundException {
    public HeavenLetterCommentNotFoundException() {
            super("하늘나라 편지에 해당 댓글이 존재하지 않거나 삭제되었습니다.");
    }

    public HeavenLetterCommentNotFoundException(int seq) {
        super("하늘나라 편지에 해당 댓글이 존재하지 않거나 삭제되었습니다."+seq);
    }
}

