package com.ttasum.memorial.domain.entity.donationStory;

import com.ttasum.memorial.domain.type.BoardType;
import com.ttasum.memorial.domain.entity.Story;
import com.ttasum.memorial.dto.donationStory.request.DonationStoryUpdateRequestDto;
import com.ttasum.memorial.domain.entity.blameText.BlameTextLetter;
import com.ttasum.memorial.domain.entity.flower.Flower;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb25_420_donation_story")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DonationStory extends Story {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "story_seq")
    private Integer id;

    @Column(name = "area_code", length = 10)
    private String areaCode;

    @Column(name = "story_title", length = 600)
    private String title;

    @Column(name = "donor_name", length = 150)
    private String donorName;

    @Column(name = "story_passcode", length = 60)
    private String passcode;

    @Column(name = "story_writer", length = 150)
    private String writer;

    @Column(name = "anonymity_flag", length = 1)
    private String anonymityFlag;

    @Column(name = "read_count")
    private Integer readCount;

    @Lob // 긴 문자열 데이터를 저장
    @Column(name = "story_contents", columnDefinition = "TEXT")
    private String contents;

    @Column(name = "file_name", length = 600)
    private String fileName;

    @Column(name = "org_file_name", length = 600)
    private String originalFileName;

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

    @Column(name = "del_flag", length = 1,
            columnDefinition = "varchar(1) default 'N'", nullable = false)
    private String delFlag = "N";

//    @OneToOne(mappedBy = "donationStory", cascade = CascadeType.ALL)
//    private BlameTextLetter blameTextLetter;

    @Getter
    @Transient
    private final BoardType boardType = BoardType.DONATION;

    @Column(name = "letter_paper", nullable = false)
    private Integer letterPaper;

    @Column(name = "letter_font", nullable = false)
    private Integer letterFont;

    // 꽃 정보 저장하기 위해 fk추가
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flower_seq")
    private Flower flower;

    @Builder
    public DonationStory(String areaCode, String title, String donorName,
                         String passcode, String writer, String anonymityFlag,
                         Integer readCount, String contents, String fileName,
                         String originalFileName, String writerId, String modifierId,
                         Integer letterPaper, Integer letterFont) {
        this.areaCode = areaCode;
        this.title = title;
        this.donorName = donorName;
        this.passcode = passcode;
        this.writer = writer;
        this.anonymityFlag = anonymityFlag;
        this.readCount = readCount != null ? readCount : 0;
        this.contents = contents;
        this.fileName = fileName;
        this.originalFileName = originalFileName;
        this.writerId = writerId;
        this.modifierId = modifierId;
        this.delFlag = "N";
        this.letterPaper = letterPaper != null ? letterPaper : 0;
        this.letterFont = letterFont != null ? letterFont : 0;
    }

    public void increaseReadCount() {
        if (this.readCount == null) {
            this.readCount = 1;
        }else {
            this.readCount++;
        }
    }

    public void update(DonationStoryUpdateRequestDto dto) {
        this.title = dto.getStoryTitle();
        this.writer = dto.getStoryWriter();
        this.areaCode = dto.getAreaCode();
        this.contents = dto.getStoryContents();
        this.fileName = dto.getFileName();
        this.originalFileName = dto.getOrgFileName();
        this.modifierId = dto.getModifierId();
        this.passcode = dto.getStoryPasscode();
        this.letterPaper = dto.getLetterPaper();
        this.letterFont = dto.getLetterFont();
    }

    public void delete(String modifierId) {
        this.delFlag = "Y";
        this.modifierId = modifierId;
        this.modifyTime = LocalDateTime.now();
    }

}
