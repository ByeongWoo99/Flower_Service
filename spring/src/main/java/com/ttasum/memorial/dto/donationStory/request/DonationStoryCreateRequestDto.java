package com.ttasum.memorial.dto.donationStory.request;

import com.ttasum.memorial.domain.entity.donationStory.DonationStory;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.*;

/**
 * 기증후 스토리 등록 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
public class DonationStoryCreateRequestDto {

    @NotBlank(message = "작성자는 필수 입력값입니다.")
    @Size(max = 150, message = "작성자는 최대 150자까지 입력할 수 있습니다.")
    private String storyWriter;

    @NotBlank(message = "익명 여부는 필수 입력값입니다.")
    @Size(max = 1, message = "익명 여부는 1자리(Y/N)여야 합니다.")
    private String anonymityFlag;

    @NotBlank(message = "패스워드는 필수 입력값입니다.")
    @Size(min = 8, max = 16, message = "비밀번호는 최소 8자리 최대 16자리입니다.")
    @Pattern(
            regexp = "(?=.*[A-Za-z]).{8,}",
            message = "비밀번호에 영문자를 최소 한 글자 포함해야 합니다."
    )
    private String storyPasscode;

    @NotBlank(message = "제목은 필수 입력값입니다.")
    @Size(max = 600, message = "제목은 최대 600자까지 입력할 수 있습니다.")
    private String storyTitle;

    @NotBlank(message = "지역 코드는 필수 입력값입니다.")
    @Size(max = 10, message = "지역 코드는 최대 10자까지 입력할 수 있습니다.")
    private String areaCode;

    @NotBlank(message = "본문 내용은 필수 입력값입니다.")
    private String storyContents;

    @Size(max = 600, message = "원본 파일명은 최대 600자까지 입력할 수 있습니다.")
    private String orgFileName;

//    @NotBlank(message = "캡차 토큰이 필요합니다.")
    private String captchaToken;

    @Size(max = 150)
    private String donorName;

    @Size(max = 600)
    private String fileName;

    @Size(max = 60)
    private String writerId;

    @Min(value = 0, message = "letterPaper는 0 이상이어야 합니다.")
    @NotNull(message = "letterPaper는 필수 항목입니다.")
    private Integer letterPaper;

    @Min(value = 0, message = "letterFont는 0 이상이어야 합니다.")
    @NotNull(message = "letterFont는 필수 항목입니다.")
    private Integer letterFont;


    // DTO → Entity 변환
    public DonationStory toEntity() {
        return DonationStory.builder()
                .areaCode(this.areaCode)
                .title(this.storyTitle)
                .donorName(this.donorName)
                .passcode(this.storyPasscode)
                .writer(this.storyWriter)
                .anonymityFlag(this.anonymityFlag)
                .contents(this.storyContents)
                .fileName(this.fileName)
                .originalFileName(this.orgFileName)
                .writerId(this.writerId)
                .letterPaper(this.letterPaper != null ? this.letterPaper : 0)
                .letterFont(this.letterFont != null ? this.letterFont : 0)
                .build();
    }
}
