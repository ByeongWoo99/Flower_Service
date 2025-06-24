package com.ttasum.memorial.domain.repository.notice;

import com.ttasum.memorial.domain.entity.notice.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

@NoRepositoryBean
public interface NoticeRepositoryCustom {

    // 조회수 증가
    void increaseHitCount(String boardCode, Integer articleSeq);

    /**
     * 페이징 + 검색 조회 메서드
     * @param boardCodes 조회할 게시판 코드
     * @param searchField 검색 대상 필드 ("title", "contents", "all")
     * @param keyword 검색어
     * @param pageable 페이징 정보
     */
    Page<Notice> searchNotices(List<String> boardCodes, String searchField, String keyword, Pageable pageable);
}
