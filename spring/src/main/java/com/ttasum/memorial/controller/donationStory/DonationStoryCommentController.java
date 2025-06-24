package com.ttasum.memorial.controller.donationStory;

import com.ttasum.memorial.dto.common.ApiResponse;
import com.ttasum.memorial.dto.donationStoryComment.request.DonationStoryCommentCreateRequestDto;
import com.ttasum.memorial.dto.donationStoryComment.request.DonationStoryCommentDeleteRequestDto;
import com.ttasum.memorial.dto.donationStoryComment.request.DonationStoryCommentUpdateRequestDto;
import com.ttasum.memorial.service.blameText.BlameTextPersistenceService;
import com.ttasum.memorial.service.donationStory.DonationStoryCommentService;
import com.ttasum.memorial.domain.entity.donationStory.DonationStoryComment;

import com.ttasum.memorial.exception.blameText.BlameTextException;
import com.ttasum.memorial.service.forbiddenWord.TestReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.ttasum.memorial.domain.type.BoardType.DONATION;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/donationLetters")
public class DonationStoryCommentController {

    private final DonationStoryCommentService commentService;
    private final TestReviewService testReviewService;
    private final BlameTextPersistenceService blameTextPersistenceService;

    /**
     * 댓글 등록
     * @param storySeq 게시글 id
     * @param dto 댓글 등록 요청 dto
     */
    @PostMapping("/{storySeq}/comments")
    public ResponseEntity<ApiResponse> createComment(@PathVariable Integer storySeq, @RequestBody @Valid DonationStoryCommentCreateRequestDto dto) {
            log.debug("POST /donationLetters/{}/comments - 댓글 등록 요청", storySeq);
            DonationStoryComment comment = commentService.createComment(storySeq, dto);
            // 201 Created -> location 다음 동작(조회 등)을 위한 URI를 제공
//        int commentSeq = commentService.createComment(storySeq, dto);
//        URI location = URI.create(String.format("/donationLetters/%d/comments/%d", storySeq, commentSeq));
//        return ResponseEntity.created(location).build();

            // 비난글 AI 필터링 추가
            testReviewService.saveCommentFromBlameTable(comment, true, DONATION.getType());
            return ResponseEntity.ok().body(ApiResponse.ok(
                    HttpStatus.CREATED.value(),
                    "편지 댓글이 성공적으로 등록되었습니다."
            ));
    }

    /**
     * 댓글 수정(비밀번호 검증 포함)
     * @param storySeq 게시글 id
     * @param commentSeq 댓글 id
     * @param dto 댓글 수정 요청 dto
     * @return 200 OK
     */
    @PatchMapping("/{storySeq}/comments/{commentSeq}")
    public ResponseEntity<ApiResponse> updateComment(@PathVariable Integer storySeq, @PathVariable Integer commentSeq, @RequestBody @Valid DonationStoryCommentUpdateRequestDto dto) {
        log.debug("PUT /donationLetters/{}/comments/{} - 댓글 수정 요청", storySeq, commentSeq);
        DonationStoryComment comment = commentService.updateComment(storySeq, commentSeq, dto);

        // 비난글 AI 필터링 추가
        testReviewService.saveCommentFromBlameTable(comment, false, DONATION.getType());

        return ResponseEntity.ok().body(ApiResponse.ok(
                HttpStatus.OK.value(),
                "스토리 댓글이 성공적으로 수정되었습니다."
        ));
    }

    /**
     * 댓글 삭제(비밀번호 검증 포함)
     * @param storySeq 게시글 id
     * @param commentSeq 댓글 id
     * @param dto 댓글 삭제 요청 dto
     * @return 200 OK
     */
    @DeleteMapping("/{storySeq}/comments/{commentSeq}")
    public ResponseEntity<ApiResponse> deleteComment(
            @PathVariable Integer storySeq,
            @PathVariable Integer commentSeq,
            @Valid @RequestBody DonationStoryCommentDeleteRequestDto dto) {
        log.debug("DELETE /donationLetters/{}/comments/{} - 댓글 삭제 요청", storySeq, commentSeq);

        commentService.softDeleteComment(storySeq, commentSeq, dto);

        blameTextPersistenceService.deleteBlameTextComment(storySeq, commentSeq, DONATION.getType());
        return ResponseEntity.ok().body(ApiResponse.ok(
                HttpStatus.OK.value(),
                "스토리 댓글이 성공적으로 삭제되었습니다."
        ));
    }
}
