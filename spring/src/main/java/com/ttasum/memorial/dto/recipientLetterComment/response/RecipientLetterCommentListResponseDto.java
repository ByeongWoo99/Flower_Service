package com.ttasum.memorial.dto.recipientLetterComment.response;

import com.ttasum.memorial.domain.entity.recipientLetter.RecipientLetterComment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

// 댓글 편지 조회

public class RecipientLetterCommentListResponseDto {

        private Integer commentSeq;
        private String commentWriter;
        private String contents;
        private LocalDateTime writeTime;

        public RecipientLetterCommentListResponseDto(RecipientLetterComment recipientLetterComment) {
            this.commentSeq = recipientLetterComment.getCommentSeq();
            this.commentWriter = recipientLetterComment.getCommentWriter();
            this.contents = recipientLetterComment.getContents();
            this.writeTime = recipientLetterComment.getWriteTime();
        }
    }


