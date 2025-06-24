package com.ttasum.memorial.dto.notice.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ttasum.memorial.domain.entity.notice.Notice;

import java.time.LocalDate;
import java.time.LocalDateTime;

// 공지사항 응답용 DTO
public record NoticeResponseDto(
        String boardCode,    // 게시판 코드
        Integer articleSeq,  // 게시글 번호
        String title,        // 제목
        boolean isFixed,     // 상단 고정 여부 (fixFlag == "Y")
        LocalDate writeDate, // 작성 일자 (날짜만)
        boolean isNew        // 최근 3일 이내 게시글 여부
) {
    /**
     * 엔티티 -> DTO 변환 메서드
     * @param entity 공지사항 엔티티
     * @return 반환된 dto
     */
    public static NoticeResponseDto fromEntity(Notice entity) {
        return new NoticeResponseDto(
                entity.getId().getBoardCode(),
                entity.getId().getArticleSeq(),
                entity.getTitle(),
                "Y".equalsIgnoreCase(entity.getFixFlag()),
                entity.getWriteTime().toLocalDate(),
                entity.getWriteTime().isAfter(LocalDateTime.now().minusDays(3))
        );
    }
}

