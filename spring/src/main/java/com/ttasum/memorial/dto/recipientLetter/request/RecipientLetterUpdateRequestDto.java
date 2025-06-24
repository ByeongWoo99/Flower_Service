package com.ttasum.memorial.dto.recipientLetter.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class RecipientLetterUpdateRequestDto {

    @NotNull(message = "필수 입력값이 누락되었습니다")
    private Integer letterSeq;

    @NotBlank(message = "필수 입력값이 누락되었습니다.")
    private String letterWriter;

    private String anonymityFlag;

    @NotBlank(message = "필수 입력값이 누락되었습니다.")
    private String letterPasscode;

    // 장기 코드 (ex. ORGAN001)
    private String organCode;

    // 장기 기타 내용 (선택)
    private String organEtc;

    // 수혜 연도
    private String recipientYear;

    @NotBlank(message = "필수 입력값이 누락되었습니다.")
    private String storyTitle;

    @NotBlank(message = "필수 입력값이 누락되었습니다.")
    private String letterContents;

    // 첨부 파일 이름
    private String orgFileName;

    private String fileName;

    @Min(value = 0, message = "편지지의 코드는 0~3 사이여야 합니다.")
    @Max(value = 3, message = "편지지의 코드는 0~3 사이여야 합니다.")
    private int letterPaper;

    @Min(value = 0, message = "글꼴의 코드는 0~2 사이여야 합니다.")
    @Max(value = 2, message = "글꼴의 코드는 0~2 사이여야 합니다.")
    private int letterFont;

    private String writerId;
}

