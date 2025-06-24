package com.ttasum.memorial.dto.heavenLetter.request;


import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

//수정 인증
@Getter
@Setter

public class HeavenLetterVerifyRequestDto {

    @NotNull(message = "필수 입력값이 누락되었습니다.")
    private Integer letterSeq;

    @NotBlank(message = "필수 입력값이 누락되었습니다.")
    private String letterPasscode;
}
