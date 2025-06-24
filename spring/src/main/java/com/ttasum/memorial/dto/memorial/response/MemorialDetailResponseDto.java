package com.ttasum.memorial.dto.memorial.response;

import com.ttasum.memorial.annotation.ConvertGender;
import com.ttasum.memorial.annotation.FormatDate;
import com.ttasum.memorial.annotation.MaskNameIfAnonymous;
import com.ttasum.memorial.domain.entity.memorial.Memorial;
import com.ttasum.memorial.dto.HeavenLetterSummaryDto;
import com.ttasum.memorial.dto.memorialComment.response.MemorialReplyResponseDto;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

// 기증자 추모관 상세 조회 시 반환용 DTO
@Getter
public class MemorialDetailResponseDto {

    private Integer donateSeq;

    @MaskNameIfAnonymous
    private String donorName;

    @FormatDate(pattern = "yyyyMMdd", output = "yyyy-MM-dd")
    private String donateDate;
    private String contents;

    @ConvertGender
    private String genderFlag;

    private Integer donateAge;

    // 이모지 카운트
    private Integer flowerCount;
    private Integer loveCount;
    private Integer seeCount;
    private Integer missCount;
    private Integer proudCount;
    private Integer hardCount;
    private Integer sadCount;
    private boolean isNew; // 최근 3일 이내 게시글 여부

    // 댓글 리스트
    private List<MemorialReplyResponseDto> replies;

    // 하늘나라 편지 정보 (편지 제목, 작성일, 조회수)
    private List<HeavenLetterSummaryDto> heavenLetters;

    @Builder
    private MemorialDetailResponseDto(
            Integer donateSeq,
            String donorName,
            String donateDate,
            String contents,
            String genderFlag,
            Integer donateAge,
            Integer flowerCount,
            Integer loveCount,
            Integer seeCount,
            Integer missCount,
            Integer proudCount,
            Integer hardCount,
            Integer sadCount,
            boolean isNew,
            List<MemorialReplyResponseDto> replies,
            List<HeavenLetterSummaryDto> heavenLetters) {
        this.donateSeq = donateSeq;
        this.donorName = donorName;
        this.donateDate = donateDate;
        this.contents = contents;
        this.genderFlag = genderFlag;
        this.donateAge = donateAge;
        this.flowerCount = flowerCount;
        this.loveCount = loveCount;
        this.seeCount = seeCount;
        this.missCount = missCount;
        this.proudCount = proudCount;
        this.hardCount = hardCount;
        this.sadCount = sadCount;
        this.isNew = isNew;
        this.replies = replies;
        this.heavenLetters = heavenLetters;
    }

    // 엔티티 -> DTO 변환
    public static MemorialDetailResponseDto of(
            Memorial memorial,
            List<MemorialReplyResponseDto> replyDtoList,
            List<HeavenLetterSummaryDto> heavenLetterList) {

        // writeTime 기준으로 “최근 3일 이내” 여부 계산
        LocalDateTime threeDaysAgo = LocalDateTime.now().minusDays(3);
        boolean isNew = memorial.getWriteTime().isAfter(threeDaysAgo);

        return MemorialDetailResponseDto.builder()
                .donateSeq(memorial.getId())
                .donorName(memorial.getDonorName())
                .donateDate(memorial.getDonateDate())
                .contents(memorial.getContents())
                .genderFlag(memorial.getGenderFlag())
                .donateAge(memorial.getDonateAge())
                .flowerCount(memorial.getFlowerCount())
                .loveCount(memorial.getLoveCount())
                .seeCount(memorial.getSeeCount())
                .missCount(memorial.getMissCount())
                .proudCount(memorial.getProudCount())
                .hardCount(memorial.getHardCount())
                .sadCount(memorial.getSadCount())
                .isNew(isNew)
                .replies(replyDtoList)
                .heavenLetters(heavenLetterList)
                .build();
    }
}
