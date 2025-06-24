package com.ttasum.memorial.dto.heavenLetter.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class MemorialSearchRequestDto {

    //이름 검색
    private String donateName;

    //시작일 검색
    private String startDate;

    //종료일 검색
    private String endDate;
}

