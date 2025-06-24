package com.ttasum.memorial.exception.notice;

import com.ttasum.memorial.exception.common.notFound.NotFoundException;

public class NoticeNotFoundException extends NotFoundException {
    public NoticeNotFoundException(String message) {
        super(message);
    }
}
