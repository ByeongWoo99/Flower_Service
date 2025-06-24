package com.ttasum.memorial.dto.flower;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
public class FlowerUpdateRequestDto {
    //꽃 정보 수정 요청 DTo

    @NotBlank(message = "꽃 이름은 필수입니다.")
    @Size(max = 20, message = "20자 이내로 입력 해야 합니다.")
    private String name;

    @NotBlank(message = "감정은 필수 입니다.")
    @Size(max = 10, message = "10자 이내로 입력 해야 합니다.")
    private String emotion;

    @NotBlank(message = "꽃말은 필수 입니다.")
    private String meaning;

    private String imgUrl;

    private String OrgFileName;

    private String delFlag;
}
