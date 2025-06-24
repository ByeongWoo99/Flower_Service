package com.ttasum.memorial.domain.repository.memorial;

import com.ttasum.memorial.domain.entity.memorial.Memorial;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemorialRepositoryCustom {
    /**
     * 동적 검색 + 페이징
     * @param donorName  기증자명 검생
     * @param startDate  검색 시작일 (YYYYMMDD 형식)
     * @param endDate    검색 종료일 (YYYYMMDD 형식)
     * @param year       연도(YYYY). 연도만 선택했을 경우 해당 연도 전체 범위로 검색
     * @param pageable   페이징 정보
     * @return 조건에 맞는 Memorial
     */
    Page<Memorial> searchMemorials(
            String donorName,
            String startDate,
            String endDate,
            String year,
            Pageable pageable
    );
}
