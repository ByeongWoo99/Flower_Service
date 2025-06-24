package com.ttasum.memorial.controller.heavenLetter;

import com.ttasum.memorial.domain.entity.heavenLetter.HeavenLetterComment;
import com.ttasum.memorial.dto.common.ApiResponse;

import com.ttasum.memorial.dto.heavenLetterComment.request.HeavenLetterCommentDeleteRequestDto;
import com.ttasum.memorial.dto.heavenLetterComment.request.HeavenLetterCommentRequestDto;
import com.ttasum.memorial.dto.heavenLetterComment.request.HeavenLetterCommentUpdateRequestDto;
import com.ttasum.memorial.dto.heavenLetterComment.request.HeavenLetterCommentVerifyRequestDto;
import com.ttasum.memorial.service.blameText.BlameTextPersistenceService;
import com.ttasum.memorial.service.forbiddenWord.TestReviewService;
import com.ttasum.memorial.service.heavenLetter.HeavenLetterCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


import static com.ttasum.memorial.domain.type.BoardType.HEAVEN;

@RestController
@RequiredArgsConstructor
@RequestMapping("/heavenLetters/{letterSeq}/comments")
public class HeavenLetterCommentController {

    private final HeavenLetterCommentService heavenLetterCommentService;
    private final TestReviewService testReviewService;
    private final BlameTextPersistenceService blameTextPersistenceService;

    //등록
    @PostMapping
    public ResponseEntity<ApiResponse> createComment(
            @PathVariable Integer letterSeq,
            @RequestBody @Valid HeavenLetterCommentRequestDto createCommentRequest) {
        HeavenLetterComment comment = heavenLetterCommentService.createComment(letterSeq, createCommentRequest);

        // 비난글 AI 필터링 추가
        testReviewService.saveCommentFromBlameTable(comment, true, HEAVEN.getType());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok(HttpStatus.CREATED.value(), "편지 댓글이 성공적으로 등록되었습니다."));
    }

    // 수정 인증
    @PostMapping("/{commentSeq}/verifyPwd")
    public ResponseEntity<ApiResponse> verifyCommentPasscode(
            @PathVariable Integer letterSeq,
            @PathVariable Integer commentSeq,
            @RequestBody @Valid HeavenLetterCommentVerifyRequestDto commentVerifyRequest) {

        heavenLetterCommentService.verifyCommentPasscode(commentSeq, commentVerifyRequest);

        return ResponseEntity.ok(ApiResponse.ok(HttpStatus.OK.value(), "비밀번호가 일치합니다."));
    }

    //댓글 수정
    @PatchMapping("/{commentSeq}")
    public ResponseEntity<ApiResponse> updateComment(
            //값을 자바 변수로 맵핑
            @PathVariable Integer letterSeq,
            @PathVariable Integer commentSeq,
            @RequestBody @Valid HeavenLetterCommentUpdateRequestDto updateCommentRequest) {

        heavenLetterCommentService.updateComment(commentSeq, updateCommentRequest);

        HeavenLetterComment comment = heavenLetterCommentService.updateComment(commentSeq, updateCommentRequest);
        // 비난글 AI 필터링 추가
        testReviewService.saveCommentFromBlameTable(comment, false, HEAVEN.getType());

        return ResponseEntity.ok(ApiResponse.ok(HttpStatus.OK.value(), "편지 댓글이 성공적으로 수정되었습니다."));
    }

    //댓글 삭제
    @DeleteMapping("/{commentSeq}")
    public ResponseEntity<ApiResponse> deleteComment(
            @PathVariable Integer letterSeq,
            @PathVariable Integer commentSeq,
            @RequestBody HeavenLetterCommentDeleteRequestDto deleteCommentRequest) {

        heavenLetterCommentService.deleteComment(commentSeq, deleteCommentRequest);

        blameTextPersistenceService.deleteBlameTextComment(letterSeq, commentSeq, HEAVEN.getType());
        return ResponseEntity.ok(ApiResponse.ok(HttpStatus.OK.value(), "편지 댓글이 성공적으로 삭제되었습니다."));
    }
}
