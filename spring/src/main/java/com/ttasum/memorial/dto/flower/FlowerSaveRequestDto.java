package com.ttasum.memorial.dto.flower;

import com.ttasum.memorial.domain.entity.flower.Flower;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
public class FlowerSaveRequestDto {
    //꽃 저장 요청 DTO

    @NotBlank(message = "꽃 이름은 필수입니다.")
    @Size(max = 20, message = "20자 이내로 입력 해야 합니다.")
    private String name;

    @NotBlank(message = "감정은 필수 입니다.")
    @Size(max = 10, message = "10자 이내로 입력 해야 합니다.")
    private String emotion;

    @NotBlank(message = "꽃말은 필수 입니다.")
    private String meaning;

    private String imgUrl;

    private String orgFileName;

    private String delFlag;

    //entity 변환, delFlag는 기본 값 설정해놔서 생략
    public Flower toEntity() {
        return Flower.builder()
                .name(this.name)
                .emotion(this.emotion)
                .meaning(this.meaning)
                .imgUrl(this.imgUrl)
                .orgFileName(this.orgFileName)
                .build();
    }
}
