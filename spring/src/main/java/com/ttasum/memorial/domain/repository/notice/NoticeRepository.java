package com.ttasum.memorial.domain.repository.notice;

import com.ttasum.memorial.domain.entity.notice.NoticeId;
import com.ttasum.memorial.domain.entity.notice.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, NoticeId>, NoticeRepositoryCustom{

    Page<Notice> findByIdBoardCodeAndDelFlag(
            String boardCode,
            String delFlag,
            Pageable pageable
    );
}