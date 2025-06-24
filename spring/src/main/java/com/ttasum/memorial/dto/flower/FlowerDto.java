package com.ttasum.memorial.dto.flower;

import com.ttasum.memorial.domain.entity.flower.Flower;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FlowerDto {
    //꽃 정보 DTO

    private Integer seq;
    private String name;
    private String emotion;
    private String meaning;
    private String imgUrl;
    private String delFlag;

    //Entity를 Dto로 반환
    public static FlowerDto fromEntity(Flower flower) {
        return new FlowerDto(
                flower.getSeq(),
                flower.getName(),
                flower.getEmotion(),
                flower.getMeaning(),
                flower.getImgUrl(),
                flower.getDelFlag()
        );
    }
}
