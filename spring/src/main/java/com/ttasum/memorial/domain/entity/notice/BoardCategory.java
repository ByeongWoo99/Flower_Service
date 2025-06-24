package com.ttasum.memorial.domain.entity.notice;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Table(name = "tb25_200_board_category")
public class BoardCategory {

    @Id
    @Column(name = "board_code", length = 20)
    private String boardCode;

    @Column(name = "board_name", length = 600, nullable = false)
    private String boardName;

    @Column(name = "board_type_code", length = 10)
    private String boardTypeCode;

    @Column(name = "file_count")
    private Integer fileCount;

    @Column(name = "html_flag", length = 1)
    private String htmlFlag;

    @Column(name = "reply_flag", length = 1)
    private String replyFlag;

    @Builder.Default
    @Column(name = "del_flag", length = 1, nullable = false)
    private String delFlag = "N";

    @CreationTimestamp
    @Column(name = "write_time", nullable = false, updatable = false)
    private LocalDateTime writeTime;

    @UpdateTimestamp
    @Column(name = "modify_time", nullable = false)
    private LocalDateTime modifyTime;

    @Column(name = "writer_id", length = 60, nullable = false)
    private String writerId;

    @Column(name = "modifier_id", length = 60, nullable = false)
    private String modifierId;

}
