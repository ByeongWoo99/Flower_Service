package com.ttasum.memorial.dto.recipientLetterComment.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class RecipientLetterCommentDeleteRequestDto {

        @NotNull(message = "필수 입력값이 누락되었습니다.")
        private Integer commentSeq;

        @NotNull(message = "필수 입력값이 누락되었습니다.")
        private Integer letterSeq;

        @NotBlank(message = "필수 입력값이 누락되었습니다.")
        private String commentPasscode;
}
