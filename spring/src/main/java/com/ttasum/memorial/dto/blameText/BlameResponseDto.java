package com.ttasum.memorial.dto.blameText;

import lombok.Data;

import java.util.List;
import java.util.Map;

// python 서버에서 전달 받을 json 응답 dto
@Data
public class BlameResponseDto {
    private String sentence;
    private int label;
    private double confidence;
    private List<Map<String, Object>> details;
}
