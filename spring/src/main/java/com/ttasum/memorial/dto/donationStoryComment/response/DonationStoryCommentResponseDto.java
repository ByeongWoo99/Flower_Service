package com.ttasum.memorial.dto.donationStoryComment.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

// 댓글 응답 dto
@Getter
public class DonationStoryCommentResponseDto {

    private final Integer id;
    private final String writer;
    private final String contents;
    private final LocalDateTime writeTime;

    @Builder
    public DonationStoryCommentResponseDto(Integer id, String writer, String contents, LocalDateTime writeTime) {
        this.id = id;
        this.writer = writer;
        this.contents = contents;
        this.writeTime = writeTime;
    }

}
