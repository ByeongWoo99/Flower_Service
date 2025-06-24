package com.ttasum.memorial.domain.entity.notice;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

// 공지사항 첨부파일 ID - 복합키: boardCode + articleSeq + fileSeq
@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class NoticeFileId implements Serializable {

    @Column(name = "article_seq", nullable = false)
    private Integer articleSeq;

    @Column(name = "board_code", length = 20, nullable = false)
    private String boardCode;

    // 파일 순번
    @Column(name = "file_seq", nullable = false)
    private Integer fileSeq;
}
