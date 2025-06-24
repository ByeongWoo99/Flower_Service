package com.ttasum.memorial.service.notice;


import com.ttasum.memorial.domain.entity.notice.Notice;
import com.ttasum.memorial.domain.entity.notice.NoticeId;
import com.ttasum.memorial.domain.repository.notice.NoticeRepository;
import com.ttasum.memorial.dto.notice.response.NoticeDetailResponseDto;
import com.ttasum.memorial.dto.notice.response.NoticeResponseDto;
import com.ttasum.memorial.exception.common.badRequest.InvalidKeywordLengthException;
import com.ttasum.memorial.exception.common.badRequest.InvalidPaginationParameterException;
import com.ttasum.memorial.exception.common.badRequest.InvalidSearchFieldException;
import com.ttasum.memorial.exception.notice.NoticeNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import javax.persistence.EntityManager;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {
    // 허용 가능한 정렬 필드 집합
    private static final Set<String> ALLOWED_SEARCH_FIELDS = Set.of("title", "contents", "all");
    private static final int MIN_KEYWORD_LENGTH = 2;
    private static final int MAX_KEYWORD_LENGTH = 100;

    private final NoticeRepository noticeRepository;
    private final EntityManager em;  // 1차 캐시 동기화용

    /**
     * 공지사항 목록 조회
     * @param boardCodes 게시판 코드 리스트
     * @param searchField 검색 대상 필드
     * @param keyword 검색어
     * @param pageable 페이징/정렬 정보
     * @return 엔티티 -> 응답 DTO 매핑
     */
    @Transactional(readOnly = true)
    @Override
    public Page<NoticeResponseDto> listNotices(List<String> boardCodes, String searchField,
                                               String keyword,
                                               Pageable pageable){
        // 페이지 번호/크기 방어
        if (pageable.getPageNumber() < 0 || pageable.getPageSize() < 1) {
            throw new InvalidPaginationParameterException("유효하지 않은 페이지 번호 또는 크기입니다. page >= 0, size >= 1 이어야 합니다.");
        }

        // searchField 검증
        if (searchField != null && !ALLOWED_SEARCH_FIELDS.contains(searchField)) {
            throw new InvalidSearchFieldException("유효하지 않은 검색 대상입니다: " + searchField);
        }

        // keyword 검증
        if (keyword != null && !keyword.isBlank()) {
            String trimmed = keyword.trim();
            if (trimmed.length() < MIN_KEYWORD_LENGTH || trimmed.length() > MAX_KEYWORD_LENGTH) {
                throw new InvalidKeywordLengthException("검색어는 " + MIN_KEYWORD_LENGTH + "자 이상 " + MAX_KEYWORD_LENGTH + "자 이하로 입력해야 합니다.");
            }
            keyword = trimmed;
        }

        // 돚억 검색 수행
        Page<Notice> notices = noticeRepository.searchNotices(boardCodes, searchField, keyword, pageable);

        return notices.map(NoticeResponseDto::fromEntity);
    }

    // 공지사항 단건 조회 (조회수 증가 + 최신 조회수 반영)
    @Transactional
    @Override
    public NoticeDetailResponseDto getNoticeById(String boardCode, Integer articleSeq) {
        NoticeId id = new NoticeId(boardCode, articleSeq);

        // 먼저 존재 여부 및 삭제 여부 확인
        Notice existing = noticeRepository.findById(id)
                .filter(n -> "N".equals(n.getDelFlag()))
                .orElseThrow(() -> new NoticeNotFoundException(
                        "공지사항을 찾을 수 없습니다. boardCode=" + boardCode + ", articleSeq=" + articleSeq));

        // 2) 조회수 증가 (벌크 업데이트)
        noticeRepository.increaseHitCount(boardCode, articleSeq);

        // 3) 벌크 업데이트 후 영속성 컨텍스트 동기화
        em.flush();
        em.clear();

        // 4) 업데이트된 엔티티 다시 조회 (delFlag 확인 포함)
        Notice updated = noticeRepository.findById(id)
                .filter(n -> "N".equals(n.getDelFlag()))
                .orElseThrow(() -> new NoticeNotFoundException(
                        "공지사항을 찾을 수 없습니다. boardCode=" + boardCode + ", articleSeq=" + articleSeq));

        // 5) DTO로 변환하여 반환
        return NoticeDetailResponseDto.fromEntity(updated);
    }


}
