package com.ttasum.memorial.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

// 하늘나라 편지 요약 정보 DTO
@Getter
@AllArgsConstructor
public class HeavenLetterSummaryDto {

    private Integer letterSeq;
    private String letterTitle;
    private LocalDateTime writeTime;
    private Integer readCount;
}
