package com.ttasum.memorial.dto.heavenLetter.response;

import com.ttasum.memorial.domain.entity.memorial.Memorial;
import com.ttasum.memorial.util.NameMaskUtil;
import lombok.*;

@Getter

public class MemorialSearchResponseDto {

    private Integer donateSeq;
    private String donorName;
    private String donateDate;
    private String genderFlag;
    private Integer donateAge;

    public MemorialSearchResponseDto(Memorial memorial) {
        this.donateSeq = memorial.getId();
        this.donorName  =  NameMaskUtil.maskDonorNameIfAnonymous(
                memorial.getDonorName(),
                memorial.getAnonymityFlag()
        );
        this.donateDate = memorial.getDonateDate();
        this.genderFlag = memorial.getGenderFlag();
        this.donateAge  = memorial.getDonateAge();
    }

    // 서비스에서 편하게 호출할 팩토리 메서드
    public static MemorialSearchResponseDto of(Memorial memorial) {
        return new MemorialSearchResponseDto(memorial);
    }
}
