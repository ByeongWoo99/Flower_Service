package com.ttasum.memorial.controller.memorial;

import com.ttasum.memorial.domain.entity.memorial.MemorialReply;
import com.ttasum.memorial.dto.common.ApiResponse;
import com.ttasum.memorial.dto.memorial.response.MemorialDetailResponseDto;
import com.ttasum.memorial.dto.memorial.response.MemorialResponseDto;
import com.ttasum.memorial.dto.memorialComment.request.MemorialReplyCreateRequestDto;
import com.ttasum.memorial.dto.memorialComment.request.MemorialReplyDeleteRequestDto;
import com.ttasum.memorial.dto.memorialComment.request.MemorialReplyUpdateRequestDto;
import com.ttasum.memorial.dto.memorialComment.response.MemorialReplyResponseDto;
import com.ttasum.memorial.service.blameText.BlameTextPersistenceService;
import com.ttasum.memorial.service.forbiddenWord.TestReviewService;
import com.ttasum.memorial.service.memorial.MemorialService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.ttasum.memorial.domain.type.BoardType.REMEMBRANCE;

@RestController
@RequestMapping("/remembrance")
@RequiredArgsConstructor
@Validated
public class MemorialController {

    private final MemorialService memorialService;
    private final TestReviewService testReviewService;
    private final BlameTextPersistenceService blameTextPersistenceService;

    /**
     * 기증자 추모관 목록 조회
     * @param donorName 기증자 이름
     * @param searchStart 시작 날짜
     * @param searchEnd 종료 날짜
     * @param sortField 정렬 조건
     * @param direction 정렬 방향
     * @return Page<> 반환
     */
    @GetMapping
    public ResponseEntity<Page<MemorialResponseDto>> getStories(
            @RequestParam(required = false) String donorName,
            @RequestParam(required = false) String searchStart,
            @RequestParam(required = false) String searchEnd,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "donateDate") String sortField,
            @RequestParam(defaultValue = "Desc") String direction
    ) {
        // 날짜 문자열 변환
        String startDate = (searchStart != null && !searchStart.isBlank())
                ? searchStart.replaceAll("-", "")
                : null;
        String endDate = (searchEnd != null && !searchEnd.isBlank())
                ? searchEnd.replaceAll("-", "")
                : null;

        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(memorialService.getMemorialList(donorName, startDate, endDate, pageable, sortField, direction));
    }

    /**
     * 기증자 추모관 상세 조회
     * @param donateSeq 게시글 번호
     * @return 상세 정보 응답 dto
     */
    @GetMapping("/{donateSeq}")
    public ResponseEntity<MemorialDetailResponseDto> getMemorialDetail(
            @PathVariable Integer donateSeq
    ) {
        return ResponseEntity.ok(memorialService.getMemorialDetail(donateSeq));
    }

    /**
     * 이모지 카운트 증가
     * @param donateSeq 게시글 번호
     * @param emoji 이모지 종류
     * @return 200 OK
     */
    @PostMapping("/{donateSeq}/emoji")
    public ResponseEntity<Void> incrementEmoji(
            @PathVariable Integer donateSeq,
            @RequestParam String emoji
    ) {
        memorialService.incrementEmoji(donateSeq, emoji);
        return ResponseEntity.ok().build();
    }

    /**
     * 기증자 추모관 댓글 등록
     * @param donateSeq 게시글 번호
     * @param request 등록 요청 dto(댓글 내용)
     */
    @PostMapping("/{donateSeq}/comments")
    public ResponseEntity<ApiResponse> createReply(
            @PathVariable Integer donateSeq,
            @RequestBody @Valid MemorialReplyCreateRequestDto request
    ) {
        MemorialReply result = memorialService.createReply(donateSeq, request);

        // 비난글 AI 필터링 추가
        testReviewService.saveCommentFromBlameTable(result, true, REMEMBRANCE.getType());

        return ResponseEntity.ok(ApiResponse.ok(
                HttpStatus.CREATED.value(),
                "댓글이 성공적으로 등록되었습니다."
        ));
    }

    /**
     * 댓글 수정
     * @param replySeq 댓글 번호
     * @param dto 수정 요청 dto
     */
    @PatchMapping("/{donateSeq}/comments/{replySeq}")
    public ResponseEntity<ApiResponse> updateReply(
            @PathVariable Integer donateSeq,
            @PathVariable Integer replySeq,
            @RequestBody @Valid MemorialReplyUpdateRequestDto dto
    ) {
        MemorialReply result = memorialService.updateReply(donateSeq, replySeq, dto);

        // 비난글 AI 필터링 추가
        testReviewService.saveCommentFromBlameTable(result, false, REMEMBRANCE.getType());

        return ResponseEntity.ok(ApiResponse.ok(
                HttpStatus.OK.value(),
                "댓글이 성공적으로 수정되었습니다."
        ));
    }

    /**
     * 댓글 삭제
     * @param replySeq 댓글 번호
     */
    @DeleteMapping("/{donateSeq}/comments/{replySeq}")
    public ResponseEntity<ApiResponse> deleteReply(
            @PathVariable Integer donateSeq,
            @PathVariable Integer replySeq,
            @Valid @RequestBody MemorialReplyDeleteRequestDto dto
    ) {
        memorialService.softDeleteReply(donateSeq,replySeq, dto);

        blameTextPersistenceService.deleteBlameTextComment(donateSeq, replySeq, REMEMBRANCE.getType());
        return ResponseEntity.ok(
                ApiResponse.ok(
                        HttpStatus.OK.value(),
                        "댓글이 성공적으로 삭제되었습니다."
                )
        );
    }

}
