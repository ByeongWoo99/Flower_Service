package com.ttasum.memorial.dto.flower;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlowerSelectResponseDto {
    //편지에 맞는 꽃 추천 후, 꽃 선택 응답 DTO

    private boolean success;
    private int     code;
    private String message;
    private FlowerDto flowerDto;
}
