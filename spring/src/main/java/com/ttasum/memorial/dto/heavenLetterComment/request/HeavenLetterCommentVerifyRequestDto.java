package com.ttasum.memorial.dto.heavenLetterComment.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
//수정 인증
@Getter
@Setter
public class HeavenLetterCommentVerifyRequestDto {


        @NotNull(message = "필수 입력값이 누락되었습니다.")
        private Integer letterSeq;

        @NotNull(message = "필수 입력값이 누락되었습니다.")
        private Integer commentSeq;

        @NotBlank(message = "필수 입력값이 누락되었습니다.")
        private String commentPasscode;
    }


