package com.ttasum.memorial.domain.entity.recipientLetter;

import com.ttasum.memorial.dto.recipientLetterComment.request.RecipientLetterCommentUpdateRequestDto;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Where(clause = "del_flag = 'N'")
@Builder
@Table(name = "tb25_431_recipient_letter_comment")
public class RecipientLetterComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_seq", nullable = false)
    private Integer commentSeq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "letter_seq")
    private RecipientLetter letterSeq;

    @Column(name = "comment_writer", length = 150)
    private String commentWriter;

    @Column(name = "comment_passcode", length = 60)
    private String commentPasscode;

    @Lob
    @Column(name = "contents", columnDefinition = "TEXT")
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

    @PrePersist
    public void prepersist() {
        writeTime = LocalDateTime.now();
        this.delFlag = "N";
    }

    //수정 메서드
    public void updateComment(RecipientLetterCommentUpdateRequestDto commentUpdateRequestDto) {

        this.commentWriter = commentUpdateRequestDto.getCommentWriter();
        //현재는 비밀번호를 바꿀 순 없지만 일단 넣어둠
        this.commentPasscode = commentUpdateRequestDto.getCommentPasscode();
        this.contents = commentUpdateRequestDto.getContents();
        this.modifyTime = LocalDateTime.now();
    }

    //삭제 메서드
    public void softDeleteComment() {
        this.delFlag = "Y";
        this.modifyTime = LocalDateTime.now();
    }
}