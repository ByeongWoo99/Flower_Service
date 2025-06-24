package com.ttasum.memorial.service.recipientLetter;

import com.ttasum.memorial.dto.flower.FlowerSelectResponseDto;
import com.ttasum.memorial.dto.recipientLetter.request.RecipientLetterRequestDto;
import com.ttasum.memorial.dto.recipientLetter.request.RecipientLetterUpdateRequestDto;
import com.ttasum.memorial.dto.recipientLetter.request.RecipientLetterVerifyRequestDto;
import com.ttasum.memorial.dto.recipientLetter.response.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 수혜자 편지 서비스 인터페이스
 * - 편지 등록, 조회(목록/상세), 검증, 수정, 삭제, 검색, 파일 업로드 기능 정의
 */
public interface RecipientLetterService {

    /* 편지 등록 */
    void createLetter(RecipientLetterRequestDto createRequestDto);

    /* 편지 전체 조회 */
    Page<RecipientLetterListResponseDto> getAllLetters(Pageable pageable);

    /* 편지 상세 조회 */
    RecipientLetterDetailResponse getLetterById(Integer letterSeq);

   /* 편지 수정/삭제 전 비밀번호 검증 */
    boolean verifyPasscode(Integer letterSeq, String passcode);

    /* 편지 수정*/
    void updateLetter(Integer letterSeq, RecipientLetterUpdateRequestDto updateRequestDto);

    /* 편지 삭제(soft delete) */
    void deleteLetter(Integer letterSeq, RecipientLetterVerifyRequestDto deleteRequest);

    /* 편지 검색 */
    Page<RecipientLetterListResponseDto> searchLetters(String type, String keyword, Pageable pageable);

    /* 파일 업로드 */
    List<Map<String, String>> uploadFiles(List<MultipartFile> files, String subFolder) throws IOException;

    // 꽃 선택
    FlowerSelectResponseDto selectFlower(Integer letterSeq, Integer flowerSeq, String passcode);
}
