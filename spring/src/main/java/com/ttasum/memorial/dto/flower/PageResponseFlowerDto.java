package com.ttasum.memorial.dto.flower;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PageResponseFlowerDto {

    private List<FlowerDto> flowers;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean lastPage;

}
