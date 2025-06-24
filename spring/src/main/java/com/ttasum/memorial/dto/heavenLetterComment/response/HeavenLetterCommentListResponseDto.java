package com.ttasum.memorial.dto.heavenLetterComment.response;

import com.ttasum.memorial.domain.entity.heavenLetter.HeavenLetterComment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class HeavenLetterCommentListResponseDto {

        private Integer commentSeq;
        private String commentWriter;
        private String contents;
        private LocalDateTime writeTime;

        public HeavenLetterCommentListResponseDto(HeavenLetterComment heavenLetterComment) {
            this.commentSeq = heavenLetterComment.getCommentSeq();
            this.commentWriter = heavenLetterComment.getCommentWriter();
            this.contents = heavenLetterComment.getContents();
            this.writeTime = heavenLetterComment.getWriteTime();
        }
    }
