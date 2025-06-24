package com.ttasum.memorial.dto.donationStory;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DonationStoryRequest {
    // 요청/응답 분리
    private String areaCode;
    private String title;
    private String donorName;
    private String passcode;
    private String writer;
    private String anonymityFlag;
    private Integer readCount;
    private String contents;
    private String fileName;
    private String originalFileName;
    private String writerId;
    private String modifierId;
}
