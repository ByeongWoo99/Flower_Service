package com.ttasum.memorial.exception.heavenLetter;

import com.ttasum.memorial.exception.common.notFound.NotFoundException;

public class MemorialNotFoundException extends NotFoundException {
    public MemorialNotFoundException() {
        super("기증자 정보를 찾을 수 없습니다.");
    }
}