package com.ttasum.memorial.dto.donationStoryComment.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

// 댓글 수정 요청 dto
@Getter
@Setter
public class DonationStoryCommentUpdateRequestDto {

//    @NotBlank(message = "작성자는 공백일 수 없습니다.")
    @Size(max = 150, message = "작성자는 최대 150자까지 입력할 수 있습니다.")
    private String commentWriter;

    @NotBlank(message = "패스워드는 필수 입력값입니다.")
    @Size(min = 8, max = 16, message = "비밀번호는 최소 8자리 최대 16자리입니다.")
    @Pattern(
            regexp = "(?=.*[A-Za-z]).{8,}",
            message = "비밀번호에 영문자를 최소 한 글자 포함해야 합니다."
    )
    private String commentPasscode;

    @NotBlank(message = "내용은 공백일 수 없습니다.")
    @Size(max = 3000, message = "내용은 최대 150자까지 입력할 수 있습니다.")
    private String contents;
}
