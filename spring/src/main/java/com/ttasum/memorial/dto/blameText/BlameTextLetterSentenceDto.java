package com.ttasum.memorial.dto.blameText;

import com.ttasum.memorial.domain.entity.blameText.BlameTextLetterSentence;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlameTextLetterSentenceDto {

    // 복합 키 필드를 평면화(실무 추천)
    private Integer letterSeq;   // 복합 키 구성요소 중 하나
    private Integer sentenceSeq; // 복합 키 구성요소 중 하나

    private String sentence;
    private Double confidence;
    private int label;
}
