package com.ttasum.memorial.service.recipientLetter;

import com.ttasum.memorial.dto.recipientLetterComment.request.RecipientLetterCommentDeleteRequestDto;
import com.ttasum.memorial.dto.recipientLetterComment.request.RecipientLetterCommentRequestDto;
import com.ttasum.memorial.dto.recipientLetterComment.request.RecipientLetterCommentUpdateRequestDto;
import com.ttasum.memorial.dto.recipientLetterComment.request.RecipientLetterCommentVerifyRequestDto;

public interface RecipientLetterCommentService {

    //댓글 등록
    void createComment(Integer pathLetterSeq, RecipientLetterCommentRequestDto createCommentRequest);

    //댓글 수정 인증(공통)
    boolean verifyCommentPasscode(RecipientLetterCommentVerifyRequestDto commentVerifyRequestDto, Integer commentSeq);

    //댓글 수정
    void updateComment(RecipientLetterCommentUpdateRequestDto commentUpdateRequestDto, Integer commentSeq);

    //댓글 삭제
    void deleteComment(RecipientLetterCommentDeleteRequestDto deleteRequestDto, Integer commentSeq);
}