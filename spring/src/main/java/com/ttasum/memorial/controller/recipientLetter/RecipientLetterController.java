package com.ttasum.memorial.controller.recipientLetter;

import com.ttasum.memorial.dto.common.ApiResponse;
import com.ttasum.memorial.dto.common.CommonPageRequest;
import com.ttasum.memorial.dto.flower.FlowerSelectResponseDto;
import com.ttasum.memorial.dto.heavenLetter.request.HeavenLetterVerifyRequestDto;
import com.ttasum.memorial.dto.recipientLetter.request.RecipientLetterRequestDto;
import com.ttasum.memorial.dto.recipientLetter.request.RecipientLetterUpdateRequestDto;
import com.ttasum.memorial.dto.recipientLetter.request.RecipientLetterVerifyRequestDto;
import com.ttasum.memorial.dto.recipientLetter.response.*;
import com.ttasum.memorial.service.recipientLetter.RecipientLetterService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recipientLetters")
public class RecipientLetterController {

    private final RecipientLetterService recipientLetterService;


    //등록
    @PostMapping
    public ResponseEntity<ApiResponse> createLetter(
            @RequestBody @Valid RecipientLetterRequestDto createRequestDto) {
        recipientLetterService.createLetter(createRequestDto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok(HttpStatus.CREATED.value(), "편지가 성공적으로 등록되었습니다."));
    }

    //단건 조회
    @GetMapping("/{letterSeq}")
    public ResponseEntity<RecipientLetterDetailResponse> getLetterById(
            @PathVariable Integer letterSeq) {
        RecipientLetterDetailResponse detailResponse = recipientLetterService.getLetterById(letterSeq);
        return ResponseEntity.status(HttpStatus.OK).body(detailResponse);
    }

    //목록(페이징 처리)
    @GetMapping
    public ResponseEntity<Page<RecipientLetterListResponseDto>> getLetters(
            @ModelAttribute CommonPageRequest commonPageRequest) {

        Pageable pageable = commonPageRequest.toPageable("letterSeq");
        Page<RecipientLetterListResponseDto> listResponse = recipientLetterService.getAllLetters(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(listResponse);
    }

    //편지 수정 인증
    @PostMapping("/{letterSeq}/verifyPwd")
    public ResponseEntity<ApiResponse> verifyPasscode(
            @PathVariable Integer letterSeq,
            @RequestBody @Valid RecipientLetterVerifyRequestDto recipientLetterVerifyRequestDto) {

        recipientLetterService.verifyPasscode(letterSeq, recipientLetterVerifyRequestDto.getLetterPasscode());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.ok(HttpStatus.OK.value(), "비밀번호가 일치합니다."));
    }

    //편지 수정
    @PatchMapping("/{letterSeq}")
    public ResponseEntity<ApiResponse> updateLetter(
            //값을 자바 변수로 맵핑
            @PathVariable Integer letterSeq,
            @RequestBody @Valid RecipientLetterUpdateRequestDto recipientLetterUpdateRequestDto) {

        recipientLetterService.updateLetter(letterSeq,recipientLetterUpdateRequestDto);

        // return "redirect://";
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.ok(HttpStatus.OK.value(), "편지가 성공적으로 등록되었습니다."));
    }

    //편지 삭제
    @DeleteMapping("/{letterSeq}")
    public ResponseEntity<ApiResponse> deleteLetter(
            @PathVariable Integer letterSeq,
            @RequestBody @Valid RecipientLetterVerifyRequestDto deleteRequest) {

        recipientLetterService.deleteLetter(letterSeq, deleteRequest);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.ok(HttpStatus.OK.value(), "편지가 정상적으로 삭제 되었습니다."));
    }

    //검색
    @GetMapping("/search")
    public ResponseEntity<Page<RecipientLetterListResponseDto>> searchLetters(
            @RequestParam(defaultValue = "전체") String type,
            @RequestParam(defaultValue = "") String keyword,
            @ModelAttribute CommonPageRequest commonPageRequest) {

        // 한글 타입을 영문으로 매핑
        type = switch (type) {
            case "제목", "title" -> "title";
            case "내용", "contents" -> "contents";
            case "전체", "all" -> "all";
            default -> "all";
        };

        Pageable pageable = commonPageRequest.toPageable("letterSeq");
        Page<RecipientLetterListResponseDto> result =
                recipientLetterService.searchLetters(type, keyword, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
    //사진 업로드
    @PostMapping("/upload-image")
    public ResponseEntity<List<Map<String, String>>> uploadFiles(
            @RequestParam("files") List<MultipartFile> files) throws IOException {
        List<Map<String, String>> resultList = recipientLetterService.uploadFiles(files, "recipientLetter");
        return ResponseEntity.ok(resultList);
    }

    //꽃 저장 메서드
    @PatchMapping("/{letterSeq}/flowers/{flowerSeq}")
    public ResponseEntity<FlowerSelectResponseDto> selectFlower(
            @PathVariable Integer letterSeq,
            @PathVariable Integer flowerSeq,
            @RequestBody @Valid HeavenLetterVerifyRequestDto dto
    ) {
        FlowerSelectResponseDto responseDto = recipientLetterService.selectFlower(letterSeq, flowerSeq, dto.getLetterPasscode());

        return ResponseEntity.ok(responseDto);
    }
}
