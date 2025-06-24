package com.ttasum.memorial.exception.recipientLetter;

import com.ttasum.memorial.exception.common.notFound.NotFoundException;

public class RecipientLetterNotFoundException extends NotFoundException {
    public RecipientLetterNotFoundException() {
        super("해당 수혜자 편지를 찾을 수 없습니다.");
    }
}
