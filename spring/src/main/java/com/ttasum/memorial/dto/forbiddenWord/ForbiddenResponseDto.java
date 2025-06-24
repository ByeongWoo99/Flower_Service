package com.ttasum.memorial.dto.forbiddenWord;

import lombok.Data;

@Data
public class ForbiddenResponseDto {
    private String sentence;
    private double confidence;
    private int flag;
}
