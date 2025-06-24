package com.ttasum.memorial.controller.recipientLetter;

import com.ttasum.memorial.dto.common.ApiResponse;
import com.ttasum.memorial.dto.recipientLetterComment.request.RecipientLetterCommentDeleteRequestDto;
import com.ttasum.memorial.dto.recipientLetterComment.request.RecipientLetterCommentRequestDto;
import com.ttasum.memorial.dto.recipientLetterComment.request.RecipientLetterCommentUpdateRequestDto;
import com.ttasum.memorial.dto.recipientLetterComment.request.RecipientLetterCommentVerifyRequestDto;
import com.ttasum.memorial.service.recipientLetter.RecipientLetterCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;



@RestController
@RequiredArgsConstructor
@RequestMapping("/recipientLetters/{letterSeq}/comments")

public class RecipientLetterCommentController {

    private final RecipientLetterCommentService recipientLetterCommentService;

    //등록
    @PostMapping
    public ResponseEntity<ApiResponse> createComment(
            @PathVariable Integer letterSeq,
            @RequestBody @Valid RecipientLetterCommentRequestDto createCommentRequest) {

        recipientLetterCommentService.createComment(letterSeq, createCommentRequest);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok(HttpStatus.CREATED.value(), "편지 댓글이 성공적으로 등록되었습니다."));
    }
    // 수정 인증
    @PostMapping("/{commentSeq}/verifyPwd")
    public ResponseEntity<ApiResponse> verifyCommentPasscode(
            @PathVariable Integer letterSeq,
            @PathVariable Integer commentSeq,
            @RequestBody @Valid RecipientLetterCommentVerifyRequestDto commentVerifyRequest) {

        recipientLetterCommentService.verifyCommentPasscode(commentVerifyRequest ,commentSeq);

        return ResponseEntity.ok(ApiResponse.ok(HttpStatus.OK.value(), "비밀번호가 일치합니다."));
    }

    //댓글 수정
    @PatchMapping("/{commentSeq}")
    public ResponseEntity<ApiResponse> updateComment(
            //값을 자바 변수로 맵핑
            @PathVariable Integer letterSeq,
            @PathVariable Integer commentSeq,
            @RequestBody @Valid RecipientLetterCommentUpdateRequestDto updateCommentRequest) {

        recipientLetterCommentService.updateComment(updateCommentRequest, commentSeq);

        return ResponseEntity.ok(ApiResponse.ok(HttpStatus.OK.value(), "편지 댓글이 성공적으로 수정되었습니다."));
    }
    // 댓글 삭제
    @DeleteMapping("/{commentSeq}")
    public ResponseEntity<ApiResponse> deleteComment(
            @PathVariable Integer letterSeq,
            @PathVariable Integer commentSeq,
            @RequestBody @Valid RecipientLetterCommentDeleteRequestDto deleteRequestDto
    ) {
        recipientLetterCommentService.deleteComment(deleteRequestDto, commentSeq);

        return ResponseEntity.ok(ApiResponse.ok(HttpStatus.OK.value(), "편지 댓글이 성공적으로 삭제되었습니다."));
    }

}
