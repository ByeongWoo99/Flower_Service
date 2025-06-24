package com.ttasum.memorial.dto.blameText;


import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlameTextCommentSentenceDto {

    private Integer letterSeq; // 복합키 구성 요소

    private Integer seq; // 복합키 구성 요소

    private String sentence;

    private Integer label;

    private Double confidence;
}
