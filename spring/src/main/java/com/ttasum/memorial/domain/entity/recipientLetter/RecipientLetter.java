package com.ttasum.memorial.domain.entity.recipientLetter;

import com.ttasum.memorial.domain.entity.flower.Flower;
import com.ttasum.memorial.dto.recipientLetter.request.RecipientLetterUpdateRequestDto;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 수혜자 편지 엔티티
 * - 테이블: tb25_430_recipient_letter
 * - 주요 기능: soft delete, 댓글 연관, 생성/수정/삭제 로직 포함
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Where(clause = "del_flag = 'N'")
@Entity
@Table(name = "tb25_430_recipient_letter")

public class RecipientLetter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "letter_seq", nullable = false)
    private Integer letterSeq;

    @Column(name = "organ_code", length = 10)
    private String organCode;

    @Column(name = "organ_etc", length = 60)
    private String organEtc;

    @Column(name = "story_title", length = 600)
    private String storyTitle;

    @Column(name = "recipient_year", length = 10)
    private String recipientYear;

    @Column(name = "letter_passcode", length = 60)
    private String letterPasscode;

    @Column(name = "letter_writer", length = 150)
    private String letterWriter;

    @Column(name = "anonymity_flag", length = 1)
    private String anonymityFlag;

    @Column(name = "read_count")
    private Integer readCount;

    @Lob
    @Column(name = "letter_contents" , columnDefinition = "TEXT")
    private String letterContents;

    @Column(name = "file_name", length = 600)
    private String fileName;

    @Column(name = "org_file_name", length = 600)
    private String orgFileName;

    @Column(name = "letter_paper", nullable = false)
    private int letterPaper;

    @Column(name = "letter_font", nullable = false)
    private int letterFont;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "write_time", nullable = false)
    private LocalDateTime writeTime;

    @Column(name = "writer_id", length = 60)
    private String writerId;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "modify_time")
    private LocalDateTime modifyTime;

    @Column(name = "modifier_id", length = 60)
    private String modifierId;

    @ColumnDefault("'N'")
    @Column(name = "del_flag", nullable = false, length = 1)
    private String delFlag;

    /* 댓글 연관 매핑 */
    @Builder.Default
    @OneToMany(mappedBy = "letterSeq")
    private List<RecipientLetterComment> comments = new ArrayList<>();

    //꽃 정보 저장 위해 추가
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flower_seq")
    private Flower flower;

    /**
     * JPA 생명주기
     * insert 직전에 실행(기본값 세팅)
     * update 할 땐 작동 안 함
     */
    @PrePersist
    public void prePersist() {
        this.writeTime = LocalDateTime.now();
        this.delFlag = "N";
        this.readCount = 0;
    }

    /**
     * 조회수 증가 로직
     */
    public void increaseReadCount() {
        if (this.readCount == null) this.readCount = 0;
        this.readCount += 1;
    }

    /**
     * 편지 내용 수정
     */
    public void updateLetterContents(RecipientLetterUpdateRequestDto recipientLetterUpdateRequestDto) {
        this.letterWriter = recipientLetterUpdateRequestDto.getLetterWriter();
        this.anonymityFlag = recipientLetterUpdateRequestDto.getAnonymityFlag();
        this.letterPasscode = recipientLetterUpdateRequestDto.getLetterPasscode();
        this.organCode = recipientLetterUpdateRequestDto.getOrganCode();
        this.organEtc = recipientLetterUpdateRequestDto.getOrganEtc();
        this.recipientYear = recipientLetterUpdateRequestDto.getRecipientYear();
        this.storyTitle = recipientLetterUpdateRequestDto.getStoryTitle();
        this.letterContents = recipientLetterUpdateRequestDto.getLetterContents();
        this.orgFileName = recipientLetterUpdateRequestDto.getOrgFileName();
        this.letterPaper = recipientLetterUpdateRequestDto.getLetterPaper();
        this.letterFont  = recipientLetterUpdateRequestDto.getLetterFont();
        this.modifyTime = LocalDateTime.now();
    }

    /**
     * 소프트 삭제 처리
     */
    public void softDelete() {
        this.delFlag = "Y";
        this.modifyTime = LocalDateTime.now();
    }
}