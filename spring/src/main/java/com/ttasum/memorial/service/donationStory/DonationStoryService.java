package com.ttasum.memorial.service.donationStory;


import com.ttasum.memorial.domain.entity.donationStory.DonationStory;
import com.ttasum.memorial.domain.entity.donationStory.DonationStoryComment;
import com.ttasum.memorial.domain.repository.donationStory.CommentCount;
import com.ttasum.memorial.domain.entity.flower.Flower;
import com.ttasum.memorial.domain.repository.donationStory.DonationStoryCommentRepository;
import com.ttasum.memorial.domain.repository.donationStory.DonationStoryRepository;
import com.ttasum.memorial.domain.repository.donationStory.DonationStoryRepositoryCustom;
import com.ttasum.memorial.domain.repository.flower.FlowerRepository;
import com.ttasum.memorial.dto.donationStory.request.DonationStoryCreateRequestDto;
import com.ttasum.memorial.dto.donationStory.request.DonationStoryUpdateRequestDto;
import com.ttasum.memorial.dto.donationStory.response.DonationStoryListResponseDto;
import com.ttasum.memorial.dto.donationStory.response.DonationStoryPasswordVerifyResponseDto;
import com.ttasum.memorial.dto.donationStory.response.DonationStoryResponseDto;
import com.ttasum.memorial.exception.common.badRequest.InvalidKeywordLengthException;
import com.ttasum.memorial.exception.common.badRequest.InvalidPaginationParameterException;
import com.ttasum.memorial.exception.common.badRequest.InvalidSearchFieldException;
import com.ttasum.memorial.exception.common.badRequest.CaptchaVerificationFailedException;
import com.ttasum.memorial.exception.donationStory.DonationStoryNotFoundException;
import com.ttasum.memorial.exception.flower.FlowerNotFoundException;
import com.ttasum.memorial.service.common.CaptchaVerifier;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ttasum.memorial.dto.flower.FlowerDto;
import com.ttasum.memorial.dto.flower.FlowerSelectResponseDto;

import java.util.*;

/**
 * 기증후 스토리 게시글의 저장 및 조회 로직을 처리하는 서비스 클래스
 */
@Service
@RequiredArgsConstructor
public class DonationStoryService {
    // 허용 가능한 정렬 필드 집합
    private static final Set<String> ALLOWED_SEARCH_FIELDS = Set.of("title", "contents", "all");
    private static final int MIN_KEYWORD_LENGTH = 2;
    private static final int MAX_KEYWORD_LENGTH = 100;

    private final DonationStoryRepository donationStoryRepository;
    private final DonationStoryCommentRepository commentRepository;
    private final CaptchaVerifier captchaVerifier;

    private final FlowerRepository flowerRepo;

    /**
     * 검색 + 페이징 조회 (추가된 메서드)
     * @param searchField ("title", "contents", "all")
     * @param keyword     검색어 (null 또는 빈 문자열 -> 전체 조회)
     * @param pageable    페이징 정보
     * @return 엔티티 -> DTO 변환 후 반환
     */
    @Transactional(readOnly = true)
    public Page<DonationStoryListResponseDto> searchStories(String searchField, String keyword, Pageable pageable) {
        // 1. 페이지 방어
        if (pageable.getPageNumber() < 0 || pageable.getPageSize() < 1) {
            throw new InvalidPaginationParameterException("유효하지 않은 페이지 번호 또는 크기입니다.");
        }

        // 2. 검색 필드 검증
        if (searchField != null && !ALLOWED_SEARCH_FIELDS.contains(searchField)) {
            throw new InvalidSearchFieldException("유효하지 않은 검색 대상입니다: " + searchField);
        }

        // 3. 검색어 길이 검증 및 trim 처리
        if (keyword != null && !keyword.isBlank()) {
            keyword = keyword.trim();
            if (keyword.length() < MIN_KEYWORD_LENGTH || keyword.length() > MAX_KEYWORD_LENGTH) {
                throw new InvalidKeywordLengthException("검색어는 " + MIN_KEYWORD_LENGTH + "자 이상 " + MAX_KEYWORD_LENGTH + "자 이하로 입력해야 합니다.");
            }
        }

        // 1. 스토리 목록 조회
        Page<DonationStory> page = donationStoryRepository.searchStories(searchField, keyword, pageable);

        // 2. 스토리 ID 목록 추출
        List<Integer> storyIds = page.getContent().stream()
                .map(DonationStory::getId)
                .toList();

        // 3. 댓글 수 조회 (Projection 기반)
        List<CommentCount> result = commentRepository.countCommentsByStoryIds(storyIds);
        Map<Integer, Integer> commentCountMap = new HashMap<>();
        for (CommentCount cc : result) {
            commentCountMap.put(cc.getStoryId(), cc.getCount().intValue());
        }

        // 4. 엔티티 → DTO 변환 (댓글 수 포함)
        return page.map(story ->
                DonationStoryListResponseDto.fromEntity(
                        story,
                        commentCountMap.getOrDefault(story.getId(), 0)
                )
        );
    }

    /**
     * 기증후 스토리 등록
     * @param dto 등록 요청용 dto
     * @return 엔티티 -> dto 변환후 반환
     */
    @Transactional
    public DonationStory createStory(DonationStoryCreateRequestDto dto){
        if (!captchaVerifier.verifyCaptcha(dto.getCaptchaToken())) {
            throw new CaptchaVerificationFailedException();
        }

        DonationStory story = dto.toEntity(); // DB 저장을 위해 Entity로 변환
        return donationStoryRepository.save(story);


    }

    /**
     * 단건 스토리 조회
     * @param storySeq 스토리 ID
     * @return 엔티티 -> dto 변환후 반환
     */
    @Transactional(readOnly = true)
    public DonationStoryResponseDto getStory(Integer storySeq){
        DonationStory story = donationStoryRepository.findByIdAndDelFlag(storySeq, "N")
                .orElseThrow(() -> new DonationStoryNotFoundException(storySeq));

        List<DonationStoryComment> comments = commentRepository
                .findByCommentSeqAndDelFlagOrderByWriteTimeAsc(storySeq, "N");

        story.increaseReadCount();

        return DonationStoryResponseDto.fromEntity(story, comments);
    }

    /**
     * 기증후 스토리 수정
     * @param storySeq 스토리 ID
     * @param dto 수정 요청 dto
     */
    @Transactional
    public DonationStory updateStory(Integer storySeq, DonationStoryUpdateRequestDto dto) {

        DonationStory story = donationStoryRepository.findByIdAndDelFlag(storySeq, "N")
                .orElseThrow(() -> new DonationStoryNotFoundException(storySeq));

        // 엔티티 수정 - Dirty Checking 으로 변경 감지
        story.update(dto);
        return story;
        // Dirty Checking 으로 자동 반영
        // donationStoryRepository.save(story);
    }

    /**
     * 비밀번호 검증
     * @param storySeq 스토리 id
     * @param inputPasscode 입력받은 비밀번호
     * @return 응답용 dto
     */
    @Transactional(readOnly = true)
    public DonationStoryPasswordVerifyResponseDto verifyStoryPasscode(Integer storySeq, String inputPasscode) {
        Optional<DonationStory> optionalStory = donationStoryRepository.findByIdAndDelFlag(storySeq, "N");

        boolean matched = optionalStory.map(story -> inputPasscode.equals(story.getPasscode()))
                .orElse(false);

        int result = matched ? 1 : 0;
        String message = matched ? "비밀번호가 일치합니다." : "비밀번호가 일치하지 않습니다.";

        return new DonationStoryPasswordVerifyResponseDto(result, message);
    }

    /**
     * 스토리 소프트 삭제 (delFlag = 'Y')
     * @param storySeq      삭제할 스토리 ID
     * @param inputPasscode 사용자가 입력한 스토리 비밀번호
     * @param modifierId    삭제 요청자 ID (로그용)
     * @return 삭제 성공(true) 또는 비밀번호 불일치(false)
     */
    @Transactional
    public boolean softDeleteStory(Integer storySeq, String inputPasscode, String modifierId) {
        DonationStory story = donationStoryRepository.findByIdAndDelFlag(storySeq, "N")
                .orElseThrow(() -> new DonationStoryNotFoundException(storySeq));
        // 비밀번호 검증
        if (!story.getPasscode().equals(inputPasscode)) {
            return false;
        }
        // 엔티티 내부에서 delFlag, modifierId, modifyTime 갱신
        story.delete(modifierId);

        return true;
    }

    //꽃 선택 메서드
    @Transactional
    public FlowerSelectResponseDto selectFlower(Integer storySeq, Integer flowerSeq, String inputPasscode) {
        DonationStory donationStory = donationStoryRepository.findByIdAndDelFlag(storySeq, "N")
                .orElseThrow(() -> new DonationStoryNotFoundException(storySeq));

        //code 변수 선언
        int code;

        boolean matched = donationStory.getPasscode().equals(inputPasscode);

        //비밀번호 성공 여부 판단
        String message = matched ? "비밀번호가 일치합니다." : "비밀번호가 일치하지 않습니다";

        //비밀번호 불 일치시 꽃 : null반환, code : BAD_REQUEST 반환
        code = HttpStatus.BAD_REQUEST.value();
        FlowerDto flowerDto = null;

        //비밀번호 일치 시
        if (matched) {
            // 선택된 꽃 저장
            Flower flower = flowerRepo.findById(flowerSeq)
                    .orElseThrow(() -> new FlowerNotFoundException("해당 하는 꽃을 찾을 수 없습니다. seq=" + flowerSeq));
            donationStory.setFlower(flower);
            code = HttpStatus.OK.value();
            flowerDto = FlowerDto.fromEntity(flower);
        }

        return new FlowerSelectResponseDto(matched, code, message, flowerDto);
    }

}