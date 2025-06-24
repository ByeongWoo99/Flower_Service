package com.ttasum.memorial.domain.entity.blameText;

import com.ttasum.memorial.domain.entity.Contents;
import lombok.*;

import javax.persistence.*;

@Getter
@Entity
//@IdClass(BlameTextLetterSentenceId.class)
@Table(name = "blame_text_letter_sentence")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(toBuilder = true)
@AllArgsConstructor
public class BlameTextLetterSentence extends Contents {

    @EmbeddedId
    private BlameTextLetterSentenceId id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String sentence;

    @Column
    private Double confidence;

    @Column(nullable = false)
    private int label;

    // BlameTextLetterSentence가 하나의 BlameTextLetter에 연결
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("letterSeq")  // EmbeddedId 내 필드와 매핑
    @JoinColumn(name = "letter_seq")
    private BlameTextLetter letter;
}