package com.ttasum.memorial.dto.heavenLetter.response;

import com.ttasum.memorial.domain.entity.heavenLetter.HeavenLetter;
import com.ttasum.memorial.domain.entity.memorial.Memorial;
import com.ttasum.memorial.dto.flower.FlowerDto;
import com.ttasum.memorial.dto.heavenLetterComment.response.HeavenLetterCommentListResponseDto;
import com.ttasum.memorial.util.NameMaskUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

//조회 - 단건
@Getter
@NoArgsConstructor
public class HeavenLetterDetailResponseDto {

        private Integer letterSeq;
        private Integer donateSeq;
        private String areaCode;
        private String letterTitle;
        //        private String letterPasscode;
        private String donorName;
        private String letterWriter;
        private String anonymityFlag;
        private Integer readCount;
        private String letterContents;
        private String fileName;
        private String orgFileName;
        private int letterPaper;
        private int letterFont;
        private LocalDateTime writeTime;
        private String writerId;
        private LocalDateTime modifyTime;
        private String modifierId;
        private String delFlag;
        private List<HeavenLetterCommentListResponseDto> comments;

        //꽃 정보 출력 위해 추가
        private FlowerDto flowerDto;

        public HeavenLetterDetailResponseDto(HeavenLetter heavenLetter) {
            this.letterSeq = heavenLetter.getId();
//            this.donateSeq = heavenLetter.getDonateSeq().getDonateSeq() != null ? heavenLetter.getDonateSeq().getDonateSeq() : null;
            Memorial memorial = heavenLetter.getDonateSeq();
            this.donateSeq = (memorial != null && memorial.getId() != null)
                    ? memorial.getId()
                    : null;
            this.areaCode = heavenLetter.getAreaCode();
            this.letterTitle = heavenLetter.getLetterTitle();
//            this.letterPasscode = heavenLetter.getLetterPasscode();
            this.donorName = NameMaskUtil.maskDonorNameIfAnonymous(
                    heavenLetter.getDonorName(),
                    heavenLetter.getAnonymityFlag()
            );
            this.letterWriter =  NameMaskUtil.maskDonorNameIfAnonymous(
                    heavenLetter.getLetterWriter(),
                    heavenLetter.getAnonymityFlag()
            );
            this.anonymityFlag = heavenLetter.getAnonymityFlag();
            this.readCount = heavenLetter.getReadCount();
            this.letterContents = heavenLetter.getContents();
            this.fileName = heavenLetter.getFileName();
            this.orgFileName = heavenLetter.getOrgFileName();
            this.letterPaper = heavenLetter.getLetterPaper();
            this.letterFont = heavenLetter.getLetterFont();
            this.writeTime = heavenLetter.getWriteTime();
            this.writerId = heavenLetter.getWriterId();
            this.modifyTime = heavenLetter.getModifyTime();
            this.modifierId = heavenLetter.getModifierId();
            this.delFlag = heavenLetter.getDelFlag();
            this.comments = heavenLetter.getComments().stream()
                    .map(HeavenLetterCommentListResponseDto::new)
                    .collect(Collectors.toList());
            //꽃 저장되어 있으면 값 가져오기
            this.flowerDto = heavenLetter.getFlower() != null
                    ? FlowerDto.fromEntity(heavenLetter.getFlower())
                    : null;
        }
    }

