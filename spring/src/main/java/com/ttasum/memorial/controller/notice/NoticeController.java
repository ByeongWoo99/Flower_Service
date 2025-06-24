package com.ttasum.memorial.controller.notice;

import com.ttasum.memorial.dto.notice.response.NoticeDetailResponseDto;
import com.ttasum.memorial.dto.notice.response.NoticeResponseDto;
import com.ttasum.memorial.service.notice.NoticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/notices")
public class NoticeController {

    private final NoticeService noticeService;

    /**
     * 공지사항 목록 조회(페이징, 검색)
     * @param option 조회 옵션 (all: 전체, notice: 공지사항만, recruit: 채용·입찰만)
     * @param searchField 검색 대상 필드 (title, contents, all)
     * @param keyword 검색어
     * @param pageable 페이징 정보
     * @return 서비스 호출 → Page<NoticeDTO> 반환
     */
    @GetMapping
    public ResponseEntity<Page<NoticeResponseDto>> listNotices(
            @RequestParam(defaultValue = "all") String option,
            @RequestParam(required = false, defaultValue = "all") String searchField,
            @RequestParam(required = false) String keyword,
            Pageable pageable){
        log.info("listNotices 호출: option={}, searchField={}, keyword={}, pageable={}", option, searchField, keyword, pageable);
        // 옵션에 따라 boardCode 리스트 결정
        List<String> boardCodes;
        switch (option) {
            case "notice":
                boardCodes = List.of("7");
                break;
            case "recruit":
                boardCodes = List.of("27");
                break;
            default: // all
                boardCodes = List.of("7", "27");
        }
        return ResponseEntity.ok(noticeService.listNotices(boardCodes, searchField, keyword, pageable));
    }

    /**
     * 단건 공지사항 상세 조회
     *
     * @param articleSeq 게시글 번호
     * @param option 조회 옵션 (notice 또는 recruit)
     * @return NoticeDetailDto
     */
    @GetMapping("/{articleSeq}")
    public ResponseEntity<NoticeDetailResponseDto> getNotice(
            @PathVariable Integer articleSeq,
            @RequestParam(defaultValue = "notice") String option) {

        // 옵션에 따라 단일 boardCode 결정
        String boardCode;
        switch (option) {
            case "1": // 공지사항
                boardCode = "7";
                break;
            case "2": // 채용입찰
                boardCode = "27";
                break;
            default:
                boardCode = "7";
        }

        // 서비스 호출 → DTO 변환된 결과 받기
        return ResponseEntity.ok(noticeService.getNoticeById(boardCode, articleSeq));
    }

}
