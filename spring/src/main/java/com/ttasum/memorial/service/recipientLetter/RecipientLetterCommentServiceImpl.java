package com.ttasum.memorial.service.recipientLetter;


import com.ttasum.memorial.domain.entity.recipientLetter.RecipientLetter;
import com.ttasum.memorial.domain.entity.recipientLetter.RecipientLetterComment;
import com.ttasum.memorial.domain.repository.recipientLetter.RecipientLetterCommentRepository;
import com.ttasum.memorial.domain.repository.recipientLetter.RecipientLetterRepository;
import com.ttasum.memorial.dto.recipientLetterComment.request.RecipientLetterCommentDeleteRequestDto;
import com.ttasum.memorial.dto.recipientLetterComment.request.RecipientLetterCommentRequestDto;
import com.ttasum.memorial.dto.recipientLetterComment.request.RecipientLetterCommentUpdateRequestDto;
import com.ttasum.memorial.dto.recipientLetterComment.request.RecipientLetterCommentVerifyRequestDto;
import com.ttasum.memorial.exception.common.conflict.AlreadyDeletedException;
import com.ttasum.memorial.exception.common.badRequest.InvalidPasscodeException;
import com.ttasum.memorial.exception.common.badRequest.PathVariableMismatchException;
import com.ttasum.memorial.exception.common.notFound.NotFoundException;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor

public class RecipientLetterCommentServiceImpl implements RecipientLetterCommentService {


    private final RecipientLetterRepository recipientLetterRepository;
    private final RecipientLetterCommentRepository recipientLetterCommentRepository;

    //등록
    @Transactional
    @Override
    public void createComment(Integer letterSeq, RecipientLetterCommentRequestDto createCommentRequest) {

        // 경로와 본문 값 불일치 (400 Bad Request)
        if(!letterSeq.equals(createCommentRequest.getLetterSeq())) {
            throw new PathVariableMismatchException("수혜자 편지 번호와 요청 번호가 일치하지 않습니다.");
        }

        // 리소스를 찾을 수 없음 (404 Not Found)
        RecipientLetter recipientLetter = recipientLetterRepository.findById(
                createCommentRequest.getLetterSeq()
        ).orElseThrow(() ->
                new NotFoundException("해당 수혜자 편지를 찾을 수 없습니다."));

        // 이미 삭제된 리소스에 대한 요청 (409 Conflict)
        if ("Y".equals(recipientLetter.getDelFlag())) {
            throw new AlreadyDeletedException("이미 삭제된 수혜자 편지입니다.");
        }

            RecipientLetterComment letterComment = RecipientLetterComment.builder()
                    .letterSeq(recipientLetter)
                    .commentWriter(createCommentRequest.getCommentWriter())
                    .commentPasscode(createCommentRequest.getCommentPasscode())
                    .contents(createCommentRequest.getContents())
                    .build();

            recipientLetterCommentRepository.save(letterComment);
    }
    //수정 인증(공통)
    @Transactional(readOnly = true)
    @Override
    public boolean verifyCommentPasscode(RecipientLetterCommentVerifyRequestDto commentVerifyRequestDto, Integer commentSeq) {

        // 경로와 본문 값 불일치 (400 Bad Request)
        if (!commentSeq.equals(commentVerifyRequestDto.getCommentSeq())) {
            throw new PathVariableMismatchException("수혜자 편지 댓글 번호와 요청 번호가 일치하지 않습니다.");
        }
        // 리소스를 찾을 수 없음 (404 Not Found)
        RecipientLetterComment recipientLetterComment = recipientLetterCommentRepository.findById(commentSeq)
                .filter(recipientVerifyComment -> recipientVerifyComment.getLetterSeq().getLetterSeq()
                        .equals(commentVerifyRequestDto.getLetterSeq()))
                .orElseThrow(() -> new NotFoundException("수혜자 편지의 댓글을 찾을 수 없습니다."));

        // 이미 삭제된 리소스에 대한 요청 - 댓글 delFlag "Y" (409 Conflict)
        if ("Y".equals(recipientLetterComment.getDelFlag())) {
            throw new AlreadyDeletedException("이미 삭제된 수혜자 편지 댓글입니다.");
        }
        // 이미 삭제된 리소스에 대한 요청 - 편지 delFlag "Y" (409 Conflict)
        if ("Y".equals(recipientLetterComment.getLetterSeq().getDelFlag())) {
            throw new AlreadyDeletedException("이미 삭제된 수혜자 편지입니다.");
        }

        // 비밀번호 인증 실패 시 예외 발생(400)
        if (!recipientLetterComment.getCommentPasscode().equals(commentVerifyRequestDto.getCommentPasscode())) {
            throw new InvalidPasscodeException();
        }
        return true;
    }

    //댓글 수정
    @Transactional
    @Override
    public void updateComment(RecipientLetterCommentUpdateRequestDto commentUpdateRequestDto, Integer commentSeq) {

        // 경로와 본문 값 불일치 (400 Bad Request)
        if (!commentSeq.equals(commentUpdateRequestDto.getCommentSeq())) {
            throw new PathVariableMismatchException("수혜자 편지 댓글 번호와 요청 번호가 일치하지 않습니다.");
        }
        // 리소스를 찾을 수 없음 (404 Not Found)
        RecipientLetterComment recipientLetterComment = recipientLetterCommentRepository.findById(commentSeq)
                .filter(recipientUpdateComment -> recipientUpdateComment.getLetterSeq().getLetterSeq()
                        .equals(commentUpdateRequestDto.getLetterSeq()))
                .orElseThrow(() -> new NotFoundException("수혜자 편지의 댓글을 찾을 수 없습니다."));

        // 이미 삭제된 리소스에 대한 요청 - 댓글 delFlag "Y" (409 Conflict)
        if ("Y".equals(recipientLetterComment.getDelFlag())) {
            throw new AlreadyDeletedException("이미 삭제된 수혜자 편지 댓글입니다.");
        }
        // 이미 삭제된 리소스에 대한 요청 - 편지 delFlag "Y" (409 Conflict)
        if ("Y".equals(recipientLetterComment.getLetterSeq().getDelFlag())) {
            throw new AlreadyDeletedException("이미 삭제된 수혜자 편지입니다.");
        }

        // 비밀번호 인증 실패 시 예외 발생(400)
        if (!recipientLetterComment.getCommentPasscode().equals(commentUpdateRequestDto.getCommentPasscode())) {
            throw new InvalidPasscodeException();
        }

        // 실제 댓글 내용 수정
        recipientLetterComment.updateComment(commentUpdateRequestDto);
    }
    @Transactional
    @Override
    public void deleteComment(RecipientLetterCommentDeleteRequestDto deleteRequestDto, Integer commentSeq) {

        if (!commentSeq.equals(deleteRequestDto.getCommentSeq())) {
            throw new PathVariableMismatchException("댓글 번호가 일치하지 않습니다.");
        }

        RecipientLetterComment recipientLetterComment = recipientLetterCommentRepository.findById(commentSeq)
                .filter(recipientDeleteComment -> recipientDeleteComment.getLetterSeq().getLetterSeq()
                        .equals(deleteRequestDto.getLetterSeq()))
                .orElseThrow(() -> new NotFoundException("해당 편지에 댓글이 존재하지 않습니다."));

        if ("Y".equals(recipientLetterComment.getDelFlag())) {
            throw new AlreadyDeletedException("이미 삭제된 수혜자 편지 댓글입니다.");
        }

        if ("Y".equals(recipientLetterComment.getLetterSeq().getDelFlag())) {
            throw new AlreadyDeletedException("이미 삭제된 수혜자 편지입니다.");
        }

        if (!recipientLetterComment.getCommentPasscode().equals(deleteRequestDto.getCommentPasscode())) {
            throw new InvalidPasscodeException();
        }

        recipientLetterComment.softDeleteComment();
    }

}
