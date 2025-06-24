package com.ttasum.memorial.domain.entity.heavenLetter;

import com.ttasum.memorial.domain.entity.Comment;
import com.ttasum.memorial.dto.heavenLetterComment.request.HeavenLetterCommentUpdateRequestDto;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Where(clause = "del_flag = 'N'")
@Table(name = "tb25_411_heaven_letter_comment")
public class HeavenLetterComment extends Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_seq", nullable = false)
    private Integer commentSeq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "letter_seq")
    private HeavenLetter letterSeq;


    @Column(name = "comment_writer", length = 150)
    private String commentWriter;


    @Column(name = "comment_passcode", length = 60)
    private String commentPasscode;

    @Lob
    @Column(name = "contents" , columnDefinition = "TEXT")
    private String contents;

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

    //JPA생명주기 중 하나 : save()로 DB에 insert되기 직전에 실행(기본값 세팅)
    //update할 땐 작동 안 함
    @PrePersist
    public void prePersist() {
        //편지 작성 시간 — 저장 전에 현재 시간으로 자동 설정
        this.writeTime = LocalDateTime.now();
        //삭제 여부 — 기본값 'N'으로 설정 (삭제 안 됨)
        this.delFlag = "N";
    }
    //수정 메서드
    public HeavenLetterComment updateComment(HeavenLetterCommentUpdateRequestDto updateCommentRequest) {
//        this.commentSeq = updateCommentRequest.getCommentSeq();
//        this.letterSeq = heavenLetter;
        this.commentWriter = updateCommentRequest.getCommentWriter();
        //현재는 비밀번호를 바꿀 순 없지만 일단 넣어둠
        this.commentPasscode = updateCommentRequest.getCommentPasscode();
        this.contents = updateCommentRequest.getContents();
        this.modifyTime = LocalDateTime.now();

        return this;
    }
    //삭제 메서드
    public void softDeleteComment() {
        this.delFlag = "Y";
        this.modifyTime = LocalDateTime.now();
    }

}

