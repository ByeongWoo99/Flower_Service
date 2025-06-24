package com.ttasum.memorial.dto.heavenLetter.response;

import com.ttasum.memorial.domain.entity.heavenLetter.HeavenLetter;
import com.ttasum.memorial.domain.entity.memorial.Memorial;
import com.ttasum.memorial.util.NameMaskUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

//조회 - 목록
@Getter
@NoArgsConstructor
public class HeavenLetterListResponseDto {

        private Integer letterSeq;
        private Integer donateSeq;
        private String letterTitle;
        private String donorName;
        private String letterWriter;
        private LocalDateTime writeTime;
        private Integer readCount;
        private Long commentCount;

        public HeavenLetterListResponseDto(HeavenLetter heavenLetter, Long commentCount) {
            this.letterSeq = heavenLetter.getId();
//            this.donateSeq = heavenLetter.getDonateSeq().getDonateSeq() != null ? heavenLetter.getDonateSeq().getDonateSeq() : null;
            Memorial memorial = heavenLetter.getDonateSeq();
            this.donateSeq = (memorial != null && memorial.getId() != null)
                    ? memorial.getId()
                    : null;
            this.letterTitle = heavenLetter.getLetterTitle();
            this.donorName = NameMaskUtil.maskDonorNameIfAnonymous(
                    heavenLetter.getDonorName(),
                    heavenLetter.getAnonymityFlag()
            );
            this.letterWriter =  NameMaskUtil.maskDonorNameIfAnonymous(
                    heavenLetter.getLetterWriter(),
                    heavenLetter.getAnonymityFlag()
            );
            this.writeTime = heavenLetter.getWriteTime();
            this.readCount = heavenLetter.getReadCount();
            this.commentCount = commentCount;

        }
        // 댓글 수 있는 정적 팩토리 메서드
        public static HeavenLetterListResponseDto fromEntity(HeavenLetter heavenLetterEntity, Long commentCount) {
            return new HeavenLetterListResponseDto(heavenLetterEntity, commentCount);
        }

        // 댓글 수 없는 기본 팩토리 메서드
        public static HeavenLetterListResponseDto fromEntity(HeavenLetter heavenLetterEntity) {
            return new HeavenLetterListResponseDto(heavenLetterEntity, 0L);

        }
    }


