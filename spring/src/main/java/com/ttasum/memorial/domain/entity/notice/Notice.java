package com.ttasum.memorial.domain.entity.notice;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tb25_210_article_dtl")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Notice {

    @EmbeddedId
    private NoticeId id; // boardCode + articleSeq

    @Column(name = "title", length = 600, nullable = false)
    private String title;

    @Lob
    @Column(name = "contents", nullable = false)
    private String contents;

    @Builder.Default
    @Column(name = "read_count")
    private Integer readCount = 0;

    @Column(name = "reply_title", length = 600)
    private String replyTitle;

    @Column(name = "reply_contents", length = 3000)
    private String replyContents;

    @Column(name = "reply_writer_id", length = 60)
    private String replyWriterId;

    @Column(name = "fix_flag", length = 1)
    private String fixFlag;

    @Column(name = "article_passcode", length = 60)
    private String articlePasscode;

    @Column(name = "article_url", length = 600)
    private String articleUrl;

    @Builder.Default
    @Column(name = "del_flag", length = 1, nullable = false)
    private String delFlag = "N";

    @CreationTimestamp // 자동 타임 스탬프 관리
    @Column(name = "write_time", nullable = false, updatable = false)
    private LocalDateTime writeTime;

    @Column(name = "writer_id", length = 60, nullable = false)
    private String writerId;

    @UpdateTimestamp // 자동 타임 스탬프 관리
    @Column(name = "modify_time", nullable = false)
    private LocalDateTime modifyTime;

    @Column(name = "modifier_id", length = 60, nullable = false)
    private String modifierId;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    /**
     * 첨부파일 리스트
     * 조인 컬럼: board_code, article_seq, file_seq
     */
    @Builder.Default
    @OneToMany(mappedBy = "notice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NoticeFile> files = new ArrayList<>();

    // 조회수 증가, 기본값이 0이므로 null 체크는 불필요
    public void increaseReadCount() {
        this.readCount += 1;
    }
}
