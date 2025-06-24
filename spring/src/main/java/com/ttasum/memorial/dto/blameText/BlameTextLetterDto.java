package com.ttasum.memorial.dto.blameText;

import com.ttasum.memorial.domain.entity.blameText.BlameTextLetter;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlameTextLetterDto {

    private Integer seq;
    private String sentence;
    private int label;
    private double confidence;
    private int sentenceLine;
    private double regulation;
    private LocalDateTime updateTime;
    private String boardType;
    private List<BlameTextLetterSentenceDto> sentences;
    private int deleteFlag;
    private Integer originSeq; // LAZY이므로 story_seq만 포함

}
