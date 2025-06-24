package com.ttasum.memorial.domain.entity.heavenLetter;

import com.ttasum.memorial.domain.entity.Story;
import com.ttasum.memorial.domain.entity.flower.Flower;
import com.ttasum.memorial.domain.type.BoardType;
import com.ttasum.memorial.domain.entity.memorial.Memorial;
import com.ttasum.memorial.dto.heavenLetter.request.HeavenLetterUpdateRequestDto;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "tb25_410_heaven_letter")
public class HeavenLetter extends Story {

    //이 필드가 PK(기본키)라는 의미
    @Id
    //DB에서 pk가 자동으로 값이 증가되는 'AUTO_INCREMENT' 컬럼임을 명시
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "letter_seq", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    //실제 DB컬럼명
    @JoinColumn(name = "donate_seq")
    //donateSeq은 필드명
    private Memorial donateSeq;

    @Column(name = "area_code", length = 10)
    private String areaCode;

    @Column(name = "letter_title", length = 600)
    private String letterTitle;

    @Column(name = "donor_name", length = 150)
    private String donorName;

    @Column(name = "letter_passcode", length = 60)
    private String letterPasscode;

    @Column(name = "letter_writer", length = 150)
    private String letterWriter;

    @Column(name = "anonymity_flag", length = 1)
    private String anonymityFlag;

    @Column(name = "read_count")
    private Integer readCount;

    @Lob
    @Column(name = "letter_contents" , columnDefinition = "LONGTEXT")
    private String contents;

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

    //댓글 엔티티
    @OneToMany(mappedBy = "letterSeq")
    @Where(clause = "del_flag = 'N'")
    private List<HeavenLetterComment> comments;

    @Getter
    @Transient
    private final BoardType boardType = BoardType.HEAVEN;

    //꽃 정보 저장위해 추가
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flower_seq")
    private Flower flower;

    //JPA생명주기 중 하나 : save()로 DB에 insert되기 직전에 실행(기본값 세팅)
    //update할 땐 작동 안 함
    @PrePersist
    public void prePersist() {
        //편지 작성 시간 — 저장 전에 현재 시간으로 자동 설정
        this.writeTime = LocalDateTime.now();
        //삭제 여부 — 기본값 'N'으로 설정 (삭제 안 됨)
        this.delFlag = "N";
        //조회수 — 처음엔 0으로 초기화
        this.readCount = 0;
    }

    //수정 메서드
    public HeavenLetter updateLetterContents(HeavenLetterUpdateRequestDto heavenLetterUpdateRequestDto, Memorial memorial) {
        this.letterWriter = heavenLetterUpdateRequestDto.getLetterWriter();
        this.donorName = heavenLetterUpdateRequestDto.getDonorName();
        this.donateSeq = memorial;
        this.areaCode = heavenLetterUpdateRequestDto.getAreaCode();
        this.letterTitle = heavenLetterUpdateRequestDto.getLetterTitle();
        this.contents = heavenLetterUpdateRequestDto.getLetterContents();
        this.anonymityFlag = heavenLetterUpdateRequestDto.getAnonymityFlag();
        this.orgFileName = heavenLetterUpdateRequestDto.getOrgFileName();
        this.letterPaper = heavenLetterUpdateRequestDto.getLetterPaper();
        this.letterFont  = heavenLetterUpdateRequestDto.getLetterFont();
        this.fileName = heavenLetterUpdateRequestDto.getFileName();
        this.modifyTime = LocalDateTime.now();
        return this;
    }

    //삭제 메서드
    public void softDelete() {
        this.delFlag = "Y";
        this.modifyTime = LocalDateTime.now();
    }

    //조회수 증가
    public void increaseReadCount() {
        if (this.readCount == null) this.readCount = 0;
        this.readCount += 1;
    }
}
