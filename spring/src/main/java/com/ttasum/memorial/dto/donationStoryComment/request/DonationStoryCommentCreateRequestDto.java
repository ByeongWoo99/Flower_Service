package com.ttasum.memorial.dto.donationStoryComment.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

// 댓글 등록 요청 dto
@Getter
@Setter
public class DonationStoryCommentCreateRequestDto {

    @NotBlank
    @Size(max = 150, message = "작성자명은 150자 이하로 입력해주세요.")
    private String commentWriter;

    @NotBlank(message = "패스워드는 필수 입력값입니다.")
    @Size(min = 8, max = 16, message = "비밀번호는 최소 8자리 최대 16자리입니다.")
    @Pattern(
            regexp = "(?=.*[A-Za-z]).{8,}",
            message = "비밀번호에 영문자를 최소 한 글자 포함해야 합니다."
    )
    private String commentPasscode;

    @NotBlank
    @Size(max = 3000, message = "댓글 내용은 3000자 이하로 입력해주세요.")
    private String contents;

//    @NotBlank(message = "캡차 토큰이 필요합니다.")
    private String captchaToken;

}
