package com.ttasum.memorial.dto.blameText;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlameTextCommentDto {

    private Integer seq;

    private String boardType;

    private Double confidence;

    private Double regulation;

    private Integer sentenceLine;

    private Integer storySeq;

    private String contents;

    private LocalDateTime updateTime;

    private Integer deleteFlag;

    private Integer label;

    private Integer originSeq; // comment 객체 대신 comment_seq만 전달

    private List<BlameTextCommentSentenceDto> comments; // 자식 엔티티 DTO 리스트
}
