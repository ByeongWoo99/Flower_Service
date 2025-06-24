package com.ttasum.memorial.service.heavenLetter;

import com.ttasum.memorial.domain.entity.heavenLetter.HeavenLetter;
import com.ttasum.memorial.domain.entity.heavenLetter.HeavenLetterComment;
import com.ttasum.memorial.domain.repository.heavenLetter.HeavenLetterCommentRepository;
import com.ttasum.memorial.domain.repository.heavenLetter.HeavenLetterRepository;

import com.ttasum.memorial.dto.heavenLetterComment.request.HeavenLetterCommentDeleteRequestDto;
import com.ttasum.memorial.dto.heavenLetterComment.request.HeavenLetterCommentRequestDto;
import com.ttasum.memorial.dto.heavenLetterComment.request.HeavenLetterCommentUpdateRequestDto;
import com.ttasum.memorial.dto.heavenLetterComment.request.HeavenLetterCommentVerifyRequestDto;
import com.ttasum.memorial.exception.common.badRequest.InvalidPasscodeException;
import com.ttasum.memorial.exception.common.badRequest.PathVariableMismatchException;
import com.ttasum.memorial.exception.common.conflict.AlreadyDeletedException;

import com.ttasum.memorial.exception.heavenLetter.HeavenLetterNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ttasum.memorial.exception.heavenLetter.HeavenLetterCommentNotFoundException;


@Service
@RequiredArgsConstructor

public class HeavenLetterCommentServiceImpl implements HeavenLetterCommentService{

    private final HeavenLetterCommentRepository commentRepository;
    private final HeavenLetterRepository heavenLetterRepository;

    //댓글 등록
    @Transactional
    @Override
    public HeavenLetterComment createComment(Integer letterSeq, HeavenLetterCommentRequestDto createCommentRequest) {

        // 경로와 본문 값 불일치 (400 Bad Request)
        if(!letterSeq.equals(createCommentRequest.getLetterSeq())) {
            throw new PathVariableMismatchException("하늘나라 편지 번호와 요청 번호가 일치하지 않습니다.");
        }

        HeavenLetter heavenLetter = heavenLetterRepository.findById(
                createCommentRequest.getLetterSeq())
                .orElseThrow(HeavenLetterNotFoundException::new);

        // 이미 삭제된 리소스에 대한 요청 (409 Conflict)
        if ("Y".equals(heavenLetter.getDelFlag())) {
            throw new AlreadyDeletedException("이미 삭제된 수혜자 편지입니다.");
        }

        HeavenLetterComment letterComment = HeavenLetterComment.builder()
                .letterSeq(heavenLetter)
                .commentWriter(createCommentRequest.getCommentWriter())
                .commentPasscode(createCommentRequest.getCommentPasscode())
                .contents(createCommentRequest.getContents())
                .build();

        return commentRepository.save(letterComment);
    }
    //수정 인증(공통)
    @Transactional(readOnly = true)
    @Override
    public boolean verifyCommentPasscode(Integer commentSeq, HeavenLetterCommentVerifyRequestDto commentVerifyRequest) {
        // 경로와 본문 값 불일치 (400 Bad Request)

        HeavenLetterComment heavenLetterComment = commentRepository.findById(commentSeq)
                .filter(heavenVerifyComment -> heavenVerifyComment.getLetterSeq().getId()
                        .equals(commentVerifyRequest.getLetterSeq()))
                .orElseThrow(HeavenLetterCommentNotFoundException::new);

        // 이미 삭제된 리소스에 대한 요청 - 댓글 delFlag "Y" (409 Conflict)
        if ("Y".equals(heavenLetterComment.getDelFlag())) {
            throw new AlreadyDeletedException("이미 삭제된 하늘나라 편지 댓글입니다.");
        }
        // 비밀번호 인증 실패 시 예외 발생(400)
        if (!heavenLetterComment.getCommentPasscode().equals(commentVerifyRequest.getCommentPasscode())) {
            throw new InvalidPasscodeException();
        }
        return true;
    }

    //댓글 수정
    @Transactional
    @Override
    public HeavenLetterComment updateComment(Integer commentSeq, HeavenLetterCommentUpdateRequestDto updateCommentRequest) {

        // 경로와 본문 값 불일치 (400 Bad Request)
        if (!commentSeq.equals(updateCommentRequest.getCommentSeq())) {
            throw new PathVariableMismatchException("하늘나라 편지 댓글 번호와 요청 번호가 일치하지 않습니다.");
        }
        // 리소스를 찾을 수 없음 (404 Not Found)
        HeavenLetterComment heavenLetterComment = commentRepository.findById(commentSeq)
                .orElseThrow(HeavenLetterCommentNotFoundException::new);

        // 이미 삭제된 리소스에 대한 요청 - 댓글 delFlag "Y" (409 Conflict)
        if ("Y".equals(heavenLetterComment.getDelFlag())) {
            throw new AlreadyDeletedException("이미 삭제된 수혜자 편지 댓글입니다.");
        }
        // 비밀번호 인증 실패 시 예외 발생(400)
        if (!heavenLetterComment.getCommentPasscode().equals(updateCommentRequest.getCommentPasscode())) {
            throw new InvalidPasscodeException();
        }

        // 실제 댓글 내용 수정
        return heavenLetterComment.updateComment(updateCommentRequest);
    }

    //댓글 삭제
    @Transactional
    @Override
    public void deleteComment(Integer commentSeq, HeavenLetterCommentDeleteRequestDto deleteCommentRequest) {

        if (!commentSeq.equals(deleteCommentRequest.getCommentSeq())) {
            throw new PathVariableMismatchException("하늘나라 편지 댓글 번호와 요청 번호가 일치하지 않습니다.");
        }
        HeavenLetterComment heavenLetterComment = commentRepository.findById(commentSeq)
                .orElseThrow(HeavenLetterCommentNotFoundException::new);

        if ("Y".equals(heavenLetterComment.getDelFlag())) {
            throw new AlreadyDeletedException("이미 삭제된 하늘나라 편지 댓글입니다.");
        }

        if ("Y".equals(heavenLetterComment.getLetterSeq().getDelFlag())) {
            throw new AlreadyDeletedException("이미 삭제된 하늘나라 편지입니다.");
        }

        if (!heavenLetterComment.getCommentPasscode().equals(deleteCommentRequest.getCommentPasscode())) {
            throw new InvalidPasscodeException();
        }

        heavenLetterComment.softDeleteComment();
    }

}

