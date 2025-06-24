package com.ttasum.memorial.dto.heavenLetter.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder

//추모관에서 등록하는 편지폼
public class HeavenLetterFormResponseDto {

        private final Integer donateSeq;
        private final String donorName;
        private final String areaCode;
    }

