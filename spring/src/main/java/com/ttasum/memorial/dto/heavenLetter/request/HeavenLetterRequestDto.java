package com.ttasum.memorial.dto.heavenLetter.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

//등록
@Getter
@Setter
public class HeavenLetterRequestDto {

@NotBlank(message = "필수 입력값이 누락되었습니다.")
private String letterWriter;

private String anonymityFlag;

@NotBlank(message = "필수 입력값이 누락되었습니다.")
@Pattern(
        regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$",
        message = "비밀번호는 영문과 숫자 포함 8자리 이상입니다"
)
private String letterPasscode;

private String donorName;

private Integer donateSeq;

private String areaCode;

@NotBlank(message = "필수 입력값이 누락되었습니다.")
private String letterTitle;

@NotBlank(message = "필수 입력값이 누락되었습니다.")
private String letterContents;

private String fileName;

private String orgFileName;

private String writerId;

@Min(value = 0, message = "편지지의 코드는 0~3 사이여야 합니다.")
@Max(value = 3, message = "편지지의 코드는 0~3 사이여야 합니다.")
private int letterPaper;

@Min(value = 0, message = "글꼴의 코드는 0~2 사이여야 합니다.")
@Max(value = 2, message = "글꼴의 코드는 0~2 사이여야 합니다.")
private int letterFont;
}




