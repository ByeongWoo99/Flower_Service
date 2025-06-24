package com.ttasum.memorial.domain.entity.notice;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

// tb25_210_article_dtl 테이블의 복합키(board_code + article_seq)를 표현
@EqualsAndHashCode // equals(), hashCode() 자동 생성
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Embeddable
public class NoticeId implements Serializable {
    @Column(name = "board_code", nullable = false, length = 20)
    private String boardCode;

    @Column(name = "article_seq", nullable = false)
    private Integer articleSeq;
}
