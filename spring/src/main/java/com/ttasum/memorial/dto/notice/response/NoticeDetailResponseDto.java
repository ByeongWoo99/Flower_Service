package com.ttasum.memorial.dto.notice.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ttasum.memorial.domain.entity.notice.Notice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public record NoticeDetailResponseDto(
        String boardCode,        // 게시판 코드
        Integer articleSeq,      // 게시글 번호
        String title,            // 제목
        String contents,         // 본문 내용
        int readCount,           // 조회수
        String fixFlag,          // 상단 고정 플래그 (Y/N)
        @JsonFormat(pattern = "yyyy-MM-dd a h:mm:ss", timezone = "Asia/Seoul")
        LocalDateTime writeTime, // 작성 일시 (포맷 적용)
        String writerId,         // 작성자 ID
        List<NoticeFileDto> files// 첨부파일 목록
) {
    /**
     * 엔티티 -> DTO 변환 메서드
     * @param notice 공지사항 엔티티
     * @return 반환된 dto
     */
    public static NoticeDetailResponseDto fromEntity(Notice notice) {
        List<NoticeFileDto> fileDtos = notice.getFiles().stream()
                .map(f -> new NoticeFileDto(f.getFileName(), f.getOrgFileName()))
                .collect(Collectors.toList());

        return new NoticeDetailResponseDto(
                notice.getId().getBoardCode(),
                notice.getId().getArticleSeq(),
                notice.getTitle(),
                notice.getContents(),
                notice.getReadCount() != null ? notice.getReadCount() : 0,
                notice.getFixFlag(),
                notice.getWriteTime(),
                notice.getWriterId(),
                fileDtos
        );
    }
}
