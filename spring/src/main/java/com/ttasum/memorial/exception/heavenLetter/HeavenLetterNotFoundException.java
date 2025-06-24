package com.ttasum.memorial.exception.heavenLetter;

import com.ttasum.memorial.exception.common.notFound.NotFoundException;

public class HeavenLetterNotFoundException extends NotFoundException {
    public HeavenLetterNotFoundException() {
        super("해당 하늘나라 편지를 찾을 수 없습니다.");
    }

    public HeavenLetterNotFoundException(int seq) {
        super("해당 하늘나라 편지를 찾을 수 없습니다."+ seq);
    }
}