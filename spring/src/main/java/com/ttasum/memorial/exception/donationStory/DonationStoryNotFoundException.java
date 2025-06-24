package com.ttasum.memorial.exception.donationStory;

import com.ttasum.memorial.exception.common.notFound.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class DonationStoryNotFoundException extends NotFoundException {
    public DonationStoryNotFoundException(Integer storySeq) {
        super("스토리를 찾을 수 없습니다. ID=" + storySeq);
    }
}

