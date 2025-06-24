package com.ttasum.memorial.service.notice;

import com.ttasum.memorial.dto.notice.response.NoticeDetailResponseDto;
import com.ttasum.memorial.dto.notice.response.NoticeResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface NoticeService {

    @Transactional(readOnly = true)
    Page<NoticeResponseDto> listNotices(List<String> boardCodes, String searchField,
                                        String keyword,
                                        Pageable pageable);

    @Transactional
    NoticeDetailResponseDto getNoticeById(String boardCode, Integer articleSeq);
}
