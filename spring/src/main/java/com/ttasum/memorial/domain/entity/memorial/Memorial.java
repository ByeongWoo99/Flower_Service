package com.ttasum.memorial.domain.entity.memorial;

import com.ttasum.memorial.domain.entity.Story;
import com.ttasum.memorial.domain.entity.heavenLetter.HeavenLetter;
import com.ttasum.memorial.domain.type.BoardType;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "tb25_400_memorial")
public class Memorial extends Story {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "donate_seq", nullable = false)
    private Integer id;

    //반대쪽 엔티티의 필드명을 기준으로 연결하는 어노테이션
    @OneToMany(mappedBy = "donateSeq")
    //HeavenLetter를 heavenLetters라는 리스트에 담아서 가져옴
    private List<HeavenLetter> heavenLetters;

    @Column(name = "donor_name", length = 150)
    private String donorName;

    @Column(name = "anonymity_flag", length = 1)
    private String anonymityFlag;

    @Column(name = "donate_title", length = 600)
    private String donateTitle;

    @Column(name = "area_code", length = 10)
    private String areaCode;

    @Lob
    @Column(name = "contents" , columnDefinition = "TEXT")
    private String contents;

    @Column(name = "file_name", length = 600)
    private String fileName;

    @Column(name = "org_file_name", length = 600)
    private String orgFileName;

    @Column(name = "writer", length = 150)
    private String writer;

    //VARCHAR(8) 'yyyyMMdd' ↔ LocalDate (converter autoApply)
    @Column(name = "donate_date", length = 8)
    private String donateDate;

    @Column(name = "gender_flag", length = 1)
    private String genderFlag;

    @Column(name = "donate_age")
    private Integer donateAge;


    @Column(name = "flower_count")
    private Integer flowerCount;


    @Column(name = "love_count")
    private Integer loveCount;


    @Column(name = "see_count")
    private Integer seeCount;


    @Column(name = "miss_count")
    private Integer missCount;


    @Column(name = "proud_count")
    private Integer proudCount;


    @Column(name = "hard_count")
    private Integer hardCount;


    @Column(name = "sad_count")
    private Integer sadCount;


    @ColumnDefault("'N'")
    @Column(name = "del_flag", nullable = false, length = 1)
    private String delFlag = "N";

    @CreationTimestamp
    @Column(name = "write_time", nullable = false, updatable = false)
    private LocalDateTime writeTime;

    @Column(name = "writer_id", length = 60)
    private String writerId;

    @UpdateTimestamp
    @Column(name = "modify_time")
    private LocalDateTime modifyTime;

    @Column(name = "modifier_id", length = 60)
    private String modifierId;

    @Column(name = "donor_birthdate")
    private LocalDate donorBirthdate;

    // 기증관 댓글과 일대다 연관관계 맺기
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "letterSeq")
    private List<MemorialReply> replies;

    @Getter
    @Transient
    private final BoardType boardType = BoardType.REMEMBRANCE;

    // 불필요한 필드(카운트 컬럼들) 제외를 위해 생성자에 빌더 설정
    @Builder
    private Memorial(Integer donateSeq, String donorName, String anonymityFlag, String donateTitle,
                     String areaCode, String contents, String fileName, String orgFileName,
                     String writer, String donateDate, String genderFlag, Integer donateAge,
                     Integer flowerCount, Integer loveCount, Integer seeCount,
                     Integer missCount, Integer proudCount, Integer hardCount, Integer sadCount,
                     String writerId, LocalDate donorBirthdate) {
        this.id = donateSeq;
        this.donorName = donorName;
        this.anonymityFlag = anonymityFlag;
        this.donateTitle = donateTitle;
        this.areaCode = areaCode;
        this.contents = contents;
        this.fileName = fileName;
        this.orgFileName = orgFileName;
        this.writer = writer;
        this.donateDate = donateDate;
        this.genderFlag = genderFlag;
        this.donateAge = donateAge;
        this.flowerCount = flowerCount != null ? flowerCount : 0;
        this.loveCount = loveCount != null ? loveCount : 0;
        this.seeCount = seeCount != null ? seeCount : 0;
        this.missCount = missCount != null ? missCount : 0;
        this.proudCount = proudCount != null ? proudCount : 0;
        this.hardCount = hardCount != null ? hardCount : 0;
        this.sadCount = sadCount != null ? sadCount : 0;
        this.writerId = writerId;
        this.delFlag = "N";
        this.donorBirthdate = donorBirthdate;
    }

    // 이모지 클릭 시 count 컬럼을 증가시키는 메서드
    public void incrementEmojiCount(String type) {
        switch (type) {
            case "flower":
                this.flowerCount = this.flowerCount + 1;
                break;
            case "love":
                this.loveCount = this.loveCount + 1;
                break;
            case "see":
                this.seeCount = this.seeCount + 1;
                break;
            case "miss":
                this.missCount = this.missCount + 1;
                break;
            case "proud":
                this.proudCount = this.proudCount + 1;
                break;
            case "hard":
                this.hardCount = this.hardCount + 1;
                break;
            case "sad":
                this.sadCount = this.sadCount + 1;
                break;
            default:
                break;
        }
    }

}