package com.ttasum.memorial.domain.entity.blameText;

import com.ttasum.memorial.domain.entity.Contents;
import com.ttasum.memorial.domain.entity.Story;
import com.ttasum.memorial.domain.entity.donationStory.DonationStory;
import com.ttasum.memorial.domain.entity.heavenLetter.HeavenLetter;
import lombok.*;
import org.hibernate.annotations.Any;
import org.hibernate.annotations.AnyMetaDef;
import org.hibernate.annotations.MetaValue;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "blame_text_letter")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BlameTextLetter extends Contents {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer seq;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String sentence;

    @Column(name = "label", nullable = false)
    private int label;

    @Column(name = "confidence", nullable = false)
    private double confidence;

    @Column(name = "sentence_line", nullable = false)
    private int sentenceLine;

    @Column(nullable = false)
    private double regulation;

    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @Column(name = "board_type", nullable = false, length = 20)
    private String boardType;

    @OneToMany(mappedBy = "letter", cascade = CascadeType.PERSIST)
    @Builder.Default
    private List<BlameTextLetterSentence> sentences = new ArrayList<>();

    @Column(name = "delete_flag", nullable = false)
    private int deleteFlag;

    @Column(name = "origin_seq", nullable = false)
    private int originSeq;

    // 여러 게시판을 참조하는 방법으로 서비스에서 찾는 것으로 해결
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "origin_seq", referencedColumnName = "story_seq", nullable = false)
//    private DonationStory donationStory;

    // 다형성 매핑
//    @Any(metaColumn = @Column(name = "board_type"))
//    @AnyMetaDef(
//            idType = "int",
//            metaType = "string",
//            metaValues = {
//                    @MetaValue(value = "HEAVEN", targetEntity = HeavenLetter.class),
//                    @MetaValue(value = "REMEMBER", targetEntity = Rememberance.class),
//                    @MetaValue(value = "RECIPIENT", targetEntity = RecipientLetter.class)
//            }
//    )
//    @JoinColumn(name = "origin_seq")
//    private Story originPost;
}
