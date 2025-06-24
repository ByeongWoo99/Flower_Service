package com.ttasum.memorial.dto.recipientLetter.response;

import com.ttasum.memorial.domain.entity.heavenLetter.HeavenLetter;
import com.ttasum.memorial.domain.entity.recipientLetter.RecipientLetter;
import com.ttasum.memorial.dto.heavenLetter.response.HeavenLetterResponseDto;
import com.ttasum.memorial.util.NameMaskUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor

public class RecipientLetterListResponseDto {
    //조회 - 목록

    private Integer letterSeq;
    private String storyTitle;
    private String letterWriter;
    private LocalDateTime writeTime;
    private Integer readCount;
    private Long commentCount;


    public RecipientLetterListResponseDto(RecipientLetter recipientLetter, Long commentCount) {
        this.letterSeq = recipientLetter.getLetterSeq();
        this.storyTitle = recipientLetter.getStoryTitle();
        this.letterWriter = NameMaskUtil.maskNameIfAnonymous(
                recipientLetter.getLetterWriter(),
                recipientLetter.getAnonymityFlag()
        );
        this.writeTime = recipientLetter.getWriteTime();
        this.readCount = recipientLetter.getReadCount();
        this.commentCount = commentCount;

    }

    public static RecipientLetterListResponseDto fromEntity(
            RecipientLetter recipientLetter,
            Long commentCount
    ) {
        return new RecipientLetterListResponseDto(recipientLetter, commentCount);
    }

    public static RecipientLetterListResponseDto fromEntity(
            RecipientLetter recipientLetter
    ) {
        return new RecipientLetterListResponseDto(recipientLetter, 0L);
    }
}
