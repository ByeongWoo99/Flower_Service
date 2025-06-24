package com.ttasum.memorial.dto.common;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Getter
@Setter
public class CommonPageRequest {

    private int page = 0;
    private int size = 10;

    public Pageable toPageable(String sortField) {
        return org.springframework.data.domain.PageRequest.of(page , size, Sort.by(Sort.Direction.DESC,sortField));
    }
}
