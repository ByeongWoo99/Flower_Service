package com.ttasum.memorial.dto.donationStory.response;

import com.ttasum.memorial.annotation.MaskNameIfAnonymous;
import com.ttasum.memorial.domain.entity.donationStory.DonationStory;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Builder
public class DonationStoryListResponseDto {
    private Integer storySeq;
    private String storyTitle;
    private String storyWriter;

    @MaskNameIfAnonymous
    private String donorName;

    private String areaCode;
    private Integer readCount;
    private LocalDateTime writeTime;
    private Integer commentCount;
    private String anonymityFlag;

    private static String truncate(String title) {
        return (title != null && title.length() > 20)
                ? title.substring(0, 20) + "…"
                : title;
    }

    public static DonationStoryListResponseDto fromEntity(DonationStory entity, int commentCount) {
        return DonationStoryListResponseDto.builder()
                .storySeq(entity.getId())
                .storyTitle(truncate(entity.getTitle()))
                .storyWriter(entity.getWriter())
                .donorName(entity.getDonorName())
                .areaCode(entity.getAreaCode())
                .readCount(entity.getReadCount())
                .writeTime(entity.getWriteTime())
                .commentCount(commentCount)
                .anonymityFlag(entity.getAnonymityFlag())
                .build();
    }

}
