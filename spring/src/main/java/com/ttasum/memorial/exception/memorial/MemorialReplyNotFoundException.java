package com.ttasum.memorial.exception.memorial;

import com.ttasum.memorial.exception.common.notFound.NotFoundException;

public class MemorialReplyNotFoundException extends NotFoundException {
    public MemorialReplyNotFoundException(Integer replySeq) {
        super("댓글을 찾을 수 없습니다. replySeq=" + replySeq);
    }
}
