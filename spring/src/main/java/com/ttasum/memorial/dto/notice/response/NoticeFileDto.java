package com.ttasum.memorial.dto.notice.response;

public record NoticeFileDto(
        String fileName,    // 저장된 파일명
        String orgFileName  // 원본 파일명
) {}
