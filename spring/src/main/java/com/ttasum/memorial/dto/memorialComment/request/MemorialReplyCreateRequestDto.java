package com.ttasum.memorial.dto.memorialComment.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

// 댓글 생성 요청용 DTO
@Getter
@Setter
public class MemorialReplyCreateRequestDto {

//    @NotBlank
    @Size(max = 150, message = "작성자명은 150자 이하로 입력해주세요.")
    private String replyWriter;

    @NotBlank(message = "댓글 내용을 입력해야 합니다.")
    @Size(max = 3000, message = "댓글 내용은 3000자 이하로 입력해주세요.")
    private String replyContents;

    @NotBlank(message = "패스워드는 필수 입력값입니다.")
    @Size(min = 8, max = 16, message = "비밀번호는 최소 8자리 최대 16자리입니다.")
    @Pattern(
            regexp = "(?=.*[A-Za-z]).{8,}",
            message = "비밀번호에 영문자를 최소 한 글자 포함해야 합니다."
    )
    private String replyPassword;

    private String replyWriterId;

    //    @NotBlank(message = "캡차 토큰이 필요합니다.")
    private String captchaToken;
}
