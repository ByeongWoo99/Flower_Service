package com.ttasum.memorial.dto.donationStoryComment.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

// 댓글 삭제 요청 dto
@Getter
@Setter
public class DonationStoryCommentDeleteRequestDto {

//    @NotBlank(message = "패스워드는 필수 입력값입니다.")
//    @Size(min = 8, max = 16, message = "비밀번호는 최소 8자리 최대 16자리입니다.")
//    @Pattern(
//            regexp = "(?=.*[A-Za-z]).{8,}",
//            message = "비밀번호에 영문자를 최소 한 글자 포함해야 합니다."
//    )
//    private String commentPasscode;
//
//    @Size(max = 150, message = "작성자는 최대 150자까지 입력할 수 있습니다.")
//    private String commentWriter;

    @NotNull(message = "필수 입력값이 누락되었습니다.")
    private Integer commentSeq;

    @NotNull(message = "필수 입력값이 누락되었습니다.")
    private Integer storySeq;

    @NotBlank(message = "필수 입력값이 누락되었습니다.")
    private String commentPasscode;

}
