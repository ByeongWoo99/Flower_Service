package com.ttasum.memorial.domain.entity.blameText;

import com.ttasum.memorial.domain.entity.Contents;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
//@IdClass(BlameTextCommentSentenceId.class)
@Entity
@Table(name = "blame_text_comment_sentence")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class BlameTextCommentSentence extends Contents {

    @EmbeddedId
    private BlameTextCommentSentenceId id;

    @Column(name = "confidence")
    private Double confidence;

    @NotNull
    @Column(name = "label", nullable = false)
    private Integer label;

    @NotNull
    @Column(name = "sentence", nullable = false)
    private String sentence;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("letterSeq")  // EmbeddedId 내 필드와 매핑
    @JoinColumn(name = "letter_seq")
    private BlameTextComment comment;

}