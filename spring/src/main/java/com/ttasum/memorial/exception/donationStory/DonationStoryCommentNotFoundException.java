package com.ttasum.memorial.exception.donationStory;

import com.ttasum.memorial.exception.common.notFound.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class DonationStoryCommentNotFoundException extends NotFoundException {
    public DonationStoryCommentNotFoundException(Integer commentSeq) {
        super("댓글을 찾을 수 없습니다. ID=" + commentSeq);}
}
