package com.ttasum.memorial.service.memorial;


import com.ttasum.memorial.domain.entity.memorial.Memorial;
import com.ttasum.memorial.domain.entity.memorial.MemorialReply;
import com.ttasum.memorial.domain.repository.heavenLetter.HeavenLetterRepository;
import com.ttasum.memorial.domain.repository.memorial.MemorialReplyRepository;
import com.ttasum.memorial.domain.repository.memorial.MemorialRepository;
import com.ttasum.memorial.dto.HeavenLetterSummaryDto;
import com.ttasum.memorial.dto.memorial.response.MemorialDetailResponseDto;
import com.ttasum.memorial.dto.memorial.response.MemorialResponseDto;
import com.ttasum.memorial.dto.memorialComment.request.MemorialReplyCreateRequestDto;
import com.ttasum.memorial.dto.memorialComment.request.MemorialReplyDeleteRequestDto;
import com.ttasum.memorial.dto.memorialComment.request.MemorialReplyUpdateRequestDto;
import com.ttasum.memorial.dto.memorialComment.response.MemorialReplyResponseDto;
import com.ttasum.memorial.exception.common.badRequest.CaptchaVerificationFailedException;
import com.ttasum.memorial.exception.common.badRequest.InvalidCommentPasscodeException;
import com.ttasum.memorial.exception.common.badRequest.InvalidPaginationParameterException;
import com.ttasum.memorial.exception.common.badRequest.InvalidSearchFieldException;
import com.ttasum.memorial.exception.memorial.MemorialNotFoundException;
import com.ttasum.memorial.exception.memorial.MemorialReplyNotFoundException;
import com.ttasum.memorial.service.common.CaptchaVerifier;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemorialService {

    private static final Set<String> ALLOWED_SORT_FIELDS =
            Set.of("donorName", "donateDate", "donorBirthdate", "writeTime");

    private final MemorialReplyRepository memorialReplyRepository;
    private final MemorialRepository memorialRepository;
    private final HeavenLetterRepository heavenLetterRepository;
    private final CaptchaVerifier captchaVerifier;


    /**
     * 기증자 추모관 목록 조회
     * @param donorName 기증자 이름
     * @param startDate 기증일자 시작(형식: YYYYMMDD)
     * @param endDate 기증일자 종료(형식: YYYYMMDD)
     * @param pageable 페이지 번호/크기, 정렬 정보
     * @param sortField 정렬 필드명 ("donorName", "donateDate" 등)
     * @param direction 정렬 방향 (Asc 또는 Desc)
     * @return DTO(Page<MemorialResponseDto>) 반환
     */
    @Transactional(readOnly = true)
    public Page<MemorialResponseDto> getMemorialList(
            String donorName, String startDate, String endDate,
            Pageable pageable, String sortField, String direction) {
        // 페이지 번호/크기 방어
        if (pageable.getPageNumber() < 0 || pageable.getPageSize() < 1) {
            throw new InvalidPaginationParameterException("유효하지 않은 페이지 번호 또는 크기입니다. page >= 0, size >= 1 이어야 합니다.");
        }

        // sortField 검증
        if (!ALLOWED_SORT_FIELDS.contains(sortField)) {
            throw new InvalidSearchFieldException("정렬 불가능한 필드: " + sortField);
        }

        // direction 검증 (대소문자 구분 없이 "asc"와 "desc"만 허용)
        Sort.Direction dir = Sort.Direction.fromString(direction);

        Sort sort = Sort.by(dir, sortField);

        // 정렬이 포함된 새로운 Pageable 생성
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        return memorialRepository.findByFilter(donorName, startDate, endDate, sortedPageable);
    }

    /**
     * 기증자 추모관 상세 조회
     * @param donateSeq 게시글 번호
     * @return 엔티티 -> DTO 변환 후 반환
     */
    @Transactional(readOnly = true)
    public MemorialDetailResponseDto getMemorialDetail(Integer donateSeq) {
        Memorial memorial = memorialRepository.findByIdAndDelFlag(donateSeq, "N")
                .orElseThrow(() -> new MemorialNotFoundException(donateSeq));

        // 댓글 목록 조회
        List<MemorialReplyResponseDto> replyDtoList = memorialReplyRepository
                .findByCommentSeqAndDelFlagOrderByReplyWriteTimeAsc(donateSeq, "N")
                .stream()
                .map(MemorialReplyResponseDto::of)
                .toList();

        // TODO: 정 기증자(donateSeq)에 해당하는 하늘나라 편지 목록 조회(HeavenLetterRepository 에서 가져와서 출력 예정)
        List<HeavenLetterSummaryDto> heavenLetterList = heavenLetterRepository
                .findByDonateSeqAndDelFlagOrderByWriteTimeDesc(memorial, "N")
                .stream()
                .map(letter -> new HeavenLetterSummaryDto(
                        letter.getId(),
                        letter.getLetterTitle(),
                        letter.getWriteTime(),
                        letter.getReadCount()))
                .collect(Collectors.toList());

        return MemorialDetailResponseDto.of(memorial, replyDtoList, heavenLetterList);
    }

    /**
     * 이모지 클릭시 카운트 증가
     * @param donateSeq 게시글 번호
     * @param emoji 이모지 종류
     */
    @Transactional
    public void incrementEmoji(Integer donateSeq, String emoji) {
        Memorial memorial = memorialRepository.findByIdAndDelFlag(donateSeq, "N")
                .orElseThrow(() -> new MemorialNotFoundException(donateSeq));

        memorial.incrementEmojiCount(emoji);
        memorialRepository.save(memorial);
    }

    /**
     * 댓글 등록
     * @param donateSeq 게시글 번호
     * @param dto 댓글 생성 요청 dto
     * @return 엔티티 -> DTO 변환 후 반환
     */
    @Transactional
    public MemorialReply createReply(Integer donateSeq, MemorialReplyCreateRequestDto dto) {
        if (!captchaVerifier.verifyCaptcha(dto.getCaptchaToken())) {
            throw new CaptchaVerificationFailedException();
        }

        Memorial memorial = memorialRepository.findByIdAndDelFlag(donateSeq, "N")
                .orElseThrow(() -> new MemorialNotFoundException(donateSeq));

        MemorialReply reply = MemorialReply.builder()
                .replyWriter(dto.getReplyWriter())
                .replyPassword(dto.getReplyPassword())
                .replyWriterId(null)
                .replyContents(dto.getReplyContents())
                .replyWriteTime(LocalDateTime.now())
                .memorial(memorial)
                .delFlag("N")
                .build();

        MemorialReply saved = memorialReplyRepository.save(reply);
        return memorialReplyRepository.save(reply);
    }

    /**
     * 댓글 수정
     * @param replySeq 댓글 번호
     * @return 엔티티 -> DTO 변환 후 반환
     */
    @Transactional
    public MemorialReply updateReply(Integer donateSeq, Integer replySeq, MemorialReplyUpdateRequestDto dto) {
        MemorialReply reply = memorialReplyRepository.findByCommentSeqAndCommentSeqAndDelFlag(donateSeq, replySeq, "N")
                .orElseThrow(() -> new MemorialReplyNotFoundException(replySeq));

        // 비밀번호 검증
        if (!reply.getReplyPassword().equals(dto.getReplyPassword())) {
            throw new InvalidCommentPasscodeException(replySeq);
        }

        return reply.updateComment(dto.getReplyContents(), dto.getModifierId()); // 로그인 연동 시 수정자 ID로 교체
    }

    /**
     * 댓글 소프트 삭제 + 비밀번호(또는 사용자 ID) 검증
     * @param donateSeq 글 번호
     * @param replySeq  댓글 번호
     * @param dto       삭제 요청 DTO
     */
    @Transactional
    public void softDeleteReply(
            Integer donateSeq,
            Integer replySeq,
            MemorialReplyDeleteRequestDto dto
    ) {
        MemorialReply reply = memorialReplyRepository
                .findByCommentSeqAndCommentSeqAndDelFlag(donateSeq, replySeq, "N")
                .orElseThrow(() -> new MemorialReplyNotFoundException(replySeq));

        // 비밀번호 검증 (익명 댓글의 경우)
        if (reply.getReplyPassword() != null) {
            if (!reply.getReplyPassword().equals(dto.getReplyPassword())) {
                throw new InvalidCommentPasscodeException(replySeq);
            }
        }

        // 도메인 메서드 호출: delFlag='Y', modifierId·modifyTime 갱신
        reply.deleteComment(dto.getReplyModifyId());
    }

}
