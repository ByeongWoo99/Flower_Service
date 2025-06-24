package com.ttasum.memorial.exception.memorial;

import com.ttasum.memorial.exception.common.notFound.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class MemorialNotFoundException extends NotFoundException {
    public MemorialNotFoundException(Integer donateSeq) {
        super("해당 기증자 추모관을 찾을 수 없습니다. donateSeq=" + donateSeq);
    }
}
