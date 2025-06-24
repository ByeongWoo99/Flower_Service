package com.ttasum.memorial.service.heavenLetter;

import com.ttasum.memorial.dto.heavenLetterComment.request.HeavenLetterCommentDeleteRequestDto;
import com.ttasum.memorial.dto.heavenLetterComment.request.HeavenLetterCommentRequestDto;
import com.ttasum.memorial.dto.heavenLetterComment.request.HeavenLetterCommentUpdateRequestDto;
import com.ttasum.memorial.dto.heavenLetterComment.request.HeavenLetterCommentVerifyRequestDto;
import com.ttasum.memorial.dto.heavenLetterComment.response.HeavenLetterCommentListResponseDto;
import com.ttasum.memorial.domain.entity.heavenLetter.HeavenLetterComment;
import com.ttasum.memorial.dto.heavenLetter.response.HeavenLetterCommentResponseDto;

public interface HeavenLetterCommentService {

    //댓글 등록
    HeavenLetterComment createComment(Integer letterSeq, HeavenLetterCommentRequestDto createCommentRequest);

    //댓글 수정 인증(공통)
    boolean verifyCommentPasscode(Integer commentSeq, HeavenLetterCommentVerifyRequestDto commentVerifyRequest);

    //댓글 수정
    HeavenLetterComment updateComment(Integer commentSeq, HeavenLetterCommentUpdateRequestDto updateCommentRequest);

    //댓글 삭제
    void deleteComment(Integer commentSeq, HeavenLetterCommentDeleteRequestDto deleteCommentRequest);
}
