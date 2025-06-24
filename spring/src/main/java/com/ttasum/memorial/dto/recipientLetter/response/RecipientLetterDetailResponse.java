package com.ttasum.memorial.dto.recipientLetter.response;

import com.ttasum.memorial.domain.entity.recipientLetter.RecipientLetter;
import com.ttasum.memorial.domain.enums.OrganCode;
import com.ttasum.memorial.dto.flower.FlowerDto;
import com.ttasum.memorial.dto.recipientLetterComment.response.RecipientLetterCommentListResponseDto;
import com.ttasum.memorial.util.NameMaskUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

//조회 단건
@Getter
@Setter
@NoArgsConstructor

public class RecipientLetterDetailResponse {
    private Integer letterSeq;
    private String organCode;
    private String organName;
    private String organEtc;
    private String storyTitle;
    private String recipientYear;
//    private String letterPasscode;
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
    private List<RecipientLetterCommentListResponseDto> comments;

    //꽃 정보 출력 위해 추가
    private FlowerDto flowerDto;

    public RecipientLetterDetailResponse(RecipientLetter recipientLetter) {
        this.letterSeq = recipientLetter.getLetterSeq();
        this.organCode = recipientLetter.getOrganCode();
        this.organName = resolveOrganName(recipientLetter);
        this.organEtc = recipientLetter.getOrganEtc();
        this.storyTitle = recipientLetter.getStoryTitle();
        this.recipientYear = recipientLetter.getRecipientYear();
//        this.letterPasscode = recipientLetter.getLetterPasscode();
        this.letterWriter = NameMaskUtil.maskNameIfAnonymous(
                recipientLetter.getLetterWriter(),
                recipientLetter.getAnonymityFlag()
        );
        this.anonymityFlag = recipientLetter.getAnonymityFlag();
        this.readCount = recipientLetter.getReadCount();
        this.letterContents = recipientLetter.getLetterContents();
        this.fileName = recipientLetter.getFileName();
        this.orgFileName = recipientLetter.getOrgFileName();
        this.letterPaper = recipientLetter.getLetterPaper();
        this.letterFont = recipientLetter.getLetterFont();
        this.writeTime = recipientLetter.getWriteTime();
        this.writerId = recipientLetter.getWriterId();
        this.modifyTime = recipientLetter.getModifyTime();
        this.modifierId = recipientLetter.getModifierId();
        this.delFlag = recipientLetter.getDelFlag();
        this.comments =  recipientLetter.getComments().stream()
                .map(RecipientLetterCommentListResponseDto::new)
                .collect(Collectors.toList());
        //꽃 저장되어 있으면 값 가져오기
        this.flowerDto = recipientLetter.getFlower() != null
                ? FlowerDto.fromEntity(recipientLetter.getFlower())
                : null;
    }
    private String resolveOrganName(RecipientLetter recipientLetter) {
        if ("ORGAN000".equals(recipientLetter.getOrganCode())) {
            return recipientLetter.getOrganEtc();
        }

            return Arrays.stream(OrganCode.values())
                    .filter(code -> code.getCode().equalsIgnoreCase(recipientLetter.getOrganCode()))
                    .findFirst()
                    .map(OrganCode::getName)
                    .orElse(null); //

    }
}

