package com.ttasum.memorial.dto.memorial.response;

import com.ttasum.memorial.annotation.ConvertGender;
import com.ttasum.memorial.annotation.FormatDate;
import com.ttasum.memorial.annotation.MaskNameIfAnonymous;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

// 기증자 추모관 목록 조회 시 반환용 DTO
@Getter
@AllArgsConstructor
public class MemorialResponseDto {

    private Integer donateSeq;

    @MaskNameIfAnonymous
    private String donorName;

    private String anonymityFlag;

    @ConvertGender
    private String genderFlag;

    private Integer donateAge;

    @FormatDate(pattern = "yyyyMMdd", output = "yyyy. MM. dd")
    private String donateDate;

    private Long commentCount;

    private LocalDate donorBirthdate;
}
