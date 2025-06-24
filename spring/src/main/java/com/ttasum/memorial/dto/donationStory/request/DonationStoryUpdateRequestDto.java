package com.ttasum.memorial.dto.donationStory.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;

@Getter
@Setter
public class DonationStoryUpdateRequestDto {

    @Size(max = 10, message = "areaCode는 최대 10글자까지 가능합니다.")
    private String areaCode;

    @NotBlank(message = "제목은 공백일 수 없습니다.")
    @Size(max = 600, message = "제목은 최대 600글자까지 가능합니다.")
    private String storyTitle;

    @NotBlank(message = "패스워드는 필수 입력값입니다.")
    @Size(min = 8, max = 16, message = "비밀번호는 최소 8자리 최대 16자리입니다.")
    @Pattern(
            regexp = "(?=.*[A-Za-z]).{8,}",
            message = "비밀번호에 영문자를 최소 한 글자 포함해야 합니다."
    )
    private String storyPasscode;

    @NotBlank(message = "작성자는 공백일 수 없습니다.")
    @Size(max = 150, message = "작성자는 최대 150글자까지 가능합니다.")
    private String storyWriter;

    @Pattern(regexp = "Y|N", message = "anonymityFlag는 'Y' 또는 'N'이어야 합니다.")
    private String anonymityFlag;

    @Min(value = 0, message = "readCount는 0 이상의 값이어야 합니다.")
    private Integer readCount;

    @NotBlank(message = "본문은 공백일 수 없습니다.")
    private String storyContents;

    @Size(max = 600, message = "fileName은 최대 600글자까지 가능합니다.")
    private String fileName;

    @Size(max = 600, message = "originalFileName은 최대 600글자까지 가능합니다.")
    private String orgFileName;

    @Size(max = 60, message = "writerId는 최대 60글자까지 가능합니다.")
    private String writerId;

    @Size(max = 60, message = "modifierId는 최대 60글자까지 가능합니다.")
    private String modifierId;

    @Min(value = 0, message = "letterPaper는 0 이상이어야 합니다.")
    @NotNull(message = "letterPaper는 필수 항목입니다.")
    private Integer letterPaper;

    @Min(value = 0, message = "letterFont는 0 이상이어야 합니다.")
    @NotNull(message = "letterFont는 필수 항목입니다.")
    private Integer letterFont;

}
