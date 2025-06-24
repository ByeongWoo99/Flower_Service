package com.ttasum.memorial.dto.flower;


import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FastApiResponseDto {
    //Fast API 서버 응답 DTO, 2개의 감정 정보를 담은 리스트
    private List<String> emotions;
}
