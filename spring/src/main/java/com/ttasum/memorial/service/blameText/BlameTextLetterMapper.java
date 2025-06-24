package com.ttasum.memorial.service.blameText;

import com.ttasum.memorial.domain.entity.blameText.BlameTextComment;
import com.ttasum.memorial.domain.entity.blameText.BlameTextCommentSentence;
import com.ttasum.memorial.domain.entity.blameText.BlameTextLetter;
import com.ttasum.memorial.domain.entity.blameText.BlameTextLetterSentence;
import com.ttasum.memorial.dto.blameText.BlameTextCommentDto;
import com.ttasum.memorial.dto.blameText.BlameTextCommentSentenceDto;
import com.ttasum.memorial.dto.blameText.BlameTextLetterDto;
import com.ttasum.memorial.dto.blameText.BlameTextLetterSentenceDto;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

public class BlameTextLetterMapper {


    public static BlameTextLetterDto toBlameTextLetterDto(BlameTextLetter entity) {
        return BlameTextLetterDto.builder()
                .seq(entity.getSeq())
                .sentence(entity.getSentence())
                .label(entity.getLabel())
                .confidence(entity.getConfidence())
                .sentenceLine(entity.getSentenceLine())
                .regulation(entity.getRegulation())
                .updateTime(entity.getUpdateTime())
                .boardType(entity.getBoardType())
                .deleteFlag(entity.getDeleteFlag())
                .originSeq(entity.getOriginSeq()) // LAZY 처리 주의
                .sentences(
                        entity.getSentences() != null ?
                                entity.getSentences().stream()
                                        .map(BlameTextLetterMapper::toBlameTextLetterSentenceDto)
                                        .collect(Collectors.toList())
                                : null
                )
                .build();
    }

    public static BlameTextLetterSentenceDto toBlameTextLetterSentenceDto(BlameTextLetterSentence entity) {
        return BlameTextLetterSentenceDto.builder()
                .letterSeq(entity.getId().getLetterSeq())
                .sentenceSeq(entity.getId().getSeq())
                .sentence(entity.getSentence())
                .confidence(entity.getConfidence())
                .label(entity.getLabel())
                .build();
    }
    public static BlameTextCommentDto toBlameTextCommentDto(BlameTextComment entity) {
        return BlameTextCommentDto.builder()
                .seq(entity.getSeq())
                .boardType(entity.getBoardType())
                .confidence(entity.getConfidence())
                .regulation(entity.getRegulation())
                .sentenceLine(entity.getSentenceLine())
                .storySeq(entity.getStorySeq())
                .contents(entity.getContents())
                .updateTime(entity.getUpdateTime())
                .deleteFlag(entity.getDeleteFlag())
                .label(entity.getLabel())
                .originSeq(entity.getOriginSeq())
                .comments(
                        entity.getComments() != null ?
                                entity.getComments().stream()
                                        .map(BlameTextLetterMapper::toBlameTextCommentSentenceDto)
                                        .collect(Collectors.toList())
                                : null
                )
                .build();
    }

    public static BlameTextCommentSentenceDto toBlameTextCommentSentenceDto(BlameTextCommentSentence entity) {
        return BlameTextCommentSentenceDto.builder()
                .letterSeq(entity.getId().getLetterSeq())
                .seq(entity.getId().getSeq())
                .sentence(entity.getSentence())
                .label(entity.getLabel())
                .confidence(entity.getConfidence())
                .build();
    }
}
