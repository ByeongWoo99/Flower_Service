package com.ttasum.memorial.dto.heavenLetter.response;

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

public class HeavenLetterCommentResponseDto {

    // 등록&수정
    private boolean success;
    private int code;
    private String message;
    //등록 - 성공 201
    public static HeavenLetterCommentResponseDto success(String message) {
        return new HeavenLetterCommentResponseDto(true, 201, message);
    }
    //등록 - 실패 400, 500
    public static HeavenLetterCommentResponseDto fail(int code, String message) {
        return new HeavenLetterCommentResponseDto(false, code, message);
    }

    //조회
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommentListResponse {
        private Integer commentSeq;
        private String commentWriter;
        private String contents;
        private LocalDateTime writeTime;

        public CommentListResponse(HeavenLetterComment heavenLetterComment) {
            this.commentSeq = heavenLetterComment.getCommentSeq();
            this.commentWriter = heavenLetterComment.getCommentWriter();
            this.contents = heavenLetterComment.getContents();
            this.writeTime = heavenLetterComment.getWriteTime();
        }
    }

    //인증&삭제
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommentVerifyResponse {
        private int result;
        private String message;

        //성공
        public static CommentVerifyResponse success(String message) {

            return new CommentVerifyResponse(1, message);
        }
        //실패(false하면 오류 나는 이유 찾기)
        public static CommentVerifyResponse fail(String message) {

            return new CommentVerifyResponse(0, message);
        }
    }
}
