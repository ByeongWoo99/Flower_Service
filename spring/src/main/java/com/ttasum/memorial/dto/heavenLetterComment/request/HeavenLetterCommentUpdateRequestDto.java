package com.ttasum.memorial.dto.heavenLetterComment.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
//수정
@Getter
@Setter
public class HeavenLetterCommentUpdateRequestDto {

        @NotNull(message = "필수 입력값이 누락되었습니다.")
        private Integer commentSeq;

        @NotNull(message = "필수 입력값이 누락되었습니다.")
        private Integer letterSeq;

        @NotBlank(message = "필수 입력값이 누락되었습니다.")
        private String commentWriter;

        @NotBlank(message = "필수 입력값이 누락되었습니다.")
        private String commentPasscode;

        @NotBlank(message = "필수 입력값이 누락되었습니다..")
        private String contents;

    }

