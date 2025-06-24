package com.ttasum.memorial.service.donationStory;

import com.ttasum.memorial.domain.entity.donationStory.DonationStory;
import com.ttasum.memorial.domain.entity.donationStory.DonationStoryComment;
import com.ttasum.memorial.domain.repository.donationStory.DonationStoryCommentRepository;
import com.ttasum.memorial.domain.repository.donationStory.DonationStoryRepository;
import com.ttasum.memorial.dto.donationStoryComment.request.DonationStoryCommentCreateRequestDto;
import com.ttasum.memorial.dto.donationStoryComment.request.DonationStoryCommentDeleteRequestDto;
import com.ttasum.memorial.dto.donationStoryComment.request.DonationStoryCommentUpdateRequestDto;
import com.ttasum.memorial.dto.donationStoryComment.response.DonationStoryCommentResponseDto;
import com.ttasum.memorial.exception.common.badRequest.CaptchaVerificationFailedException;
import com.ttasum.memorial.exception.donationStory.DonationStoryCommentNotFoundException;
import com.ttasum.memorial.exception.common.badRequest.InvalidCommentPasscodeException;
import com.ttasum.memorial.exception.donationStory.DonationStoryCommentNotFoundException;
import com.ttasum.memorial.exception.donationStory.DonationStoryNotFoundException;
import com.ttasum.memorial.service.common.CaptchaVerifier;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DonationStoryCommentService {

    private final DonationStoryRepository storyRepository;
    private final DonationStoryCommentRepository commentRepository;
    private final CaptchaVerifier captchaVerifier;


    /**
     * 댓글 등록
     * @param storySeq 스토리 id
     * @param dto      댓글 등록 요청 dto
     */
    @Transactional
    public DonationStoryComment createComment(Integer storySeq, DonationStoryCommentCreateRequestDto dto) {
        if (!captchaVerifier.verifyCaptcha(dto.getCaptchaToken())) {
            throw new CaptchaVerificationFailedException();
        }
        DonationStory story = storyRepository.findByIdAndDelFlag(storySeq, "N")
                .orElseThrow(() -> new DonationStoryNotFoundException(storySeq));

        return DonationStoryComment.builder()
                .story(story)
                .writer(dto.getCommentWriter())
                .passcode(dto.getCommentPasscode())
                .contents(dto.getContents())
                .writerId(null) // 로그인 연동 시 변경
                .modifierId(null) // 수정자도 임시 값 적용
                .build();
    }

    /**
     * 스토리 댓글 목록 조회
     * @param storySeq 스토리 id
     * @return 스토리 해당 댓글들 dto 반환
     */
    @Transactional(readOnly = true)
    public List<DonationStoryCommentResponseDto> getComments(Integer storySeq) {
        // 스토리 id로 게시글 찾고
        List<DonationStoryComment> comments = commentRepository.findByCommentSeqAndDelFlagOrderByWriteTimeAsc(storySeq, "N");

        // 해당 게시글의 댓글들을 dto형태로 변환 후 리스트에 저장
        List<DonationStoryCommentResponseDto> dtos = new ArrayList<>();
        for (DonationStoryComment comment : comments) {
            DonationStoryCommentResponseDto dto = DonationStoryCommentResponseDto.builder()
                    .id(comment.getCommentSeq())
                    .writer(comment.getWriter())
                    .contents(comment.getContents())
                    .writeTime(comment.getWriteTime())
                    .build();
            dtos.add(dto);
        }
        return dtos;
    }

    /**
     * 댓글 수정 + 비밀번호 검증
     * @param commentSeq 댓글 id
     * @param dto 댓글 수정 요청 dto
     */
    @Transactional
    public DonationStoryComment updateComment(Integer storySeq, Integer commentSeq, DonationStoryCommentUpdateRequestDto dto) {
        DonationStoryComment comment = commentRepository.findByCommentSeqAndLetterSeqAndDelFlag(commentSeq,
                        storyRepository.findByIdAndDelFlag(storySeq, "N").orElseThrow(),
                        "N")
                .orElseThrow(() -> new DonationStoryCommentNotFoundException(commentSeq));
        // 비밀번호 검증
        if (!comment.getPasscode().equals(dto.getCommentPasscode())) {
            throw new InvalidCommentPasscodeException(commentSeq);
        }

        // String modifierId = getUserIdFromToken(request); // 비회원이면 anonymous 반환
        return comment.updateComment(dto.getContents(),dto.getCommentWriter()); // 로그인 연동 시 수정자 ID로 교체
    }

    // eGov 환경 -> Spring Security + JWT 필터 사용
//    public String getLoginUserId() {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//
//        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
//            return "anonymous";
//        }
//
//        return auth.getName();
//    }

    /**
     * 댓글 소프트 삭제 + 비밀번호 검증
     * @param commentSeq 댓글 id
     * @param dto 댓글 삭제 요청 dto
     */
    @Transactional
    public void softDeleteComment(Integer storySeq, Integer commentSeq, DonationStoryCommentDeleteRequestDto dto) {
        System.out.println("story =" + storySeq);
        System.out.println("commentSeq = " + commentSeq);
        System.out.println("dto.getCommentPasscode() = " + dto.getCommentPasscode());

        DonationStory story = storyRepository.findByIdAndDelFlag(storySeq, "N")
                .orElseThrow(() -> new DonationStoryNotFoundException(storySeq));

        DonationStoryComment comment = commentRepository.findByCommentSeqAndLetterSeqAndDelFlag(commentSeq, story, "N")
                .orElseThrow(() -> new DonationStoryCommentNotFoundException(commentSeq));

        if (!comment.getPasscode().equals(dto.getCommentPasscode())) {
            throw new InvalidCommentPasscodeException(commentSeq);
        }

        comment.deleteComment(dto.getCommentSeq());
    }



}