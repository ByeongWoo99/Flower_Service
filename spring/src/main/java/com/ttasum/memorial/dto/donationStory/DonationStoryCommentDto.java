package com.ttasum.memorial.dto.donationStory;

import lombok.Getter;

// 기증후 스토리 댓글 등록 요청 dto
@Getter
public class DonationStoryCommentDto {
    private String commentWriter;   // 댓글 작성자 이름
    private String commentPasscode; // 댓글 비밀번호 (수정/삭제용)
    private String contents;        // 댓글 내용
    private String writerId;        // 생성자 ID
    private String modifierId;      // 수정자 ID (초기 등록 시 동일하게 사용)
}
