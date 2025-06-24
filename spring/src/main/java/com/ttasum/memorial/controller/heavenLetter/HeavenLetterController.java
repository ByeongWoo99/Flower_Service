package com.ttasum.memorial.controller.heavenLetter;

import com.ttasum.memorial.dto.common.ApiResponse;
import com.ttasum.memorial.dto.common.CommonPageRequest;
import com.ttasum.memorial.domain.entity.heavenLetter.HeavenLetter;
import com.ttasum.memorial.dto.common.ApiResponse;
import com.ttasum.memorial.dto.flower.FlowerSelectResponseDto;
import com.ttasum.memorial.dto.heavenLetter.request.*;
import com.ttasum.memorial.dto.heavenLetter.response.*;
import com.ttasum.memorial.service.blameText.BlameTextPersistenceService;
import com.ttasum.memorial.service.forbiddenWord.TestReviewService;
import com.ttasum.memorial.service.heavenLetter.HeavenLetterService;
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

import static com.ttasum.memorial.domain.type.BoardType.DONATION;
import static com.ttasum.memorial.domain.type.BoardType.HEAVEN;

@RestController
@RequiredArgsConstructor
@RequestMapping("/heavenLetters")
public class HeavenLetterController {

    private final HeavenLetterService heavenLetterService;
    private final TestReviewService testReviewService;
    private final BlameTextPersistenceService blameTextPersistenceService;

    //등록
    @PostMapping
    public ResponseEntity<ApiResponse> createLetter(
            @RequestBody @Valid HeavenLetterRequestDto creatRequest) {
        HeavenLetter story = heavenLetterService.createLetter(creatRequest);

        // 비난글 AI 필터링 추가
        testReviewService.saveBoardFromBlameTable(story, true, HEAVEN.getType());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok(HttpStatus.CREATED.value(), "편지가 성공적으로 등록되었습니다."));
    }

    //추모관에서 등록하는 편지폼
    @GetMapping("/new/{donateSeq}")
    public ResponseEntity<HeavenLetterFormResponseDto> getFormWithDonor(@PathVariable Integer donateSeq) {
        return ResponseEntity.ok(heavenLetterService.getFormWithDonor(donateSeq));
    }

    //단건 조회
    @GetMapping("/{letterSeq}")
    public ResponseEntity<HeavenLetterDetailResponseDto> getLetterById(
            @PathVariable Integer letterSeq) {
        HeavenLetterDetailResponseDto detailResponse = heavenLetterService.getLetterById(letterSeq);
        return ResponseEntity.status(HttpStatus.OK).body(detailResponse);
    }

    //목록(페이징 처리)
    @GetMapping
    public ResponseEntity<Page<HeavenLetterListResponseDto>> getLetters(
            @ModelAttribute CommonPageRequest commonPageRequest) {

        Pageable pageable = commonPageRequest.toPageable("letterSeq");
        Page<HeavenLetterListResponseDto> listResponse = heavenLetterService.getAllLetters(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(listResponse);
    }

    //편지 수정 인증
    @PostMapping("/{letterSeq}/verifyPwd")
    public ResponseEntity<ApiResponse> verifyPasscode(
            @PathVariable Integer letterSeq,
            @RequestBody @Valid HeavenLetterVerifyRequestDto heavenLetterVerifyRequestDto) {

        heavenLetterService.verifyPasscode(letterSeq, heavenLetterVerifyRequestDto.getLetterPasscode());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.ok(HttpStatus.OK.value(), "비밀번호가 일치합니다."));
    }

    //편지 수정 (letterUpdate.html)
    @PatchMapping("/{letterSeq}")
    public ResponseEntity<ApiResponse> updateLetter(
            //값을 자바 변수로 맵핑
            @PathVariable Integer letterSeq,
            @RequestBody @Valid HeavenLetterUpdateRequestDto heavenLetterUpdateRequestDto) {

        HeavenLetter story = heavenLetterService.updateLetter(letterSeq, heavenLetterUpdateRequestDto);

        // 비난글 AI 필터링 추가
        testReviewService.saveBoardFromBlameTable(story, false, HEAVEN.getType());

        // return "redirect://";
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.ok(HttpStatus.OK.value(), "편지가 성공적으로 등록되었습니다."));
    }

    //편지 삭제
    @DeleteMapping("/{letterSeq}")
    public ResponseEntity<ApiResponse> deleteLetter(
            @PathVariable Integer letterSeq,
            @RequestBody @Valid HeavenLetterVerifyRequestDto deleteRequest) {

        heavenLetterService.deleteLetter(letterSeq, deleteRequest);
        blameTextPersistenceService.deleteBlameTextLetter(deleteRequest.getLetterSeq(), HEAVEN.getType());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.ok(HttpStatus.OK.value(), "편지가 정상적으로 삭제 되었습니다."));
    }

    //검색
    @GetMapping("/search")
    public ResponseEntity<Page<HeavenLetterListResponseDto>> searchLetters(
            @RequestParam(defaultValue = "전체") String type,
            @RequestParam(defaultValue = "") String keyword,
            @ModelAttribute CommonPageRequest commonPageRequest) {

        // 한글 타입을 영문으로 매핑
        type = switch (type) {
            case "제목","title" -> "title";
            case "내용","contents" -> "contents";
            case "전체","all" -> "all";
            default -> "all";
        };

        Pageable pageable = commonPageRequest.toPageable("letterSeq");
        Page<HeavenLetterListResponseDto> result =
                heavenLetterService.searchLetters(type, keyword, pageable);
        return ResponseEntity.ok(result);
    }
    //사진 업로드
    @PostMapping("/upload-image")
    public ResponseEntity<List<Map<String, String>>> uploadFiles(
            @RequestParam("files") List<MultipartFile> files) throws IOException {
        List<Map<String, String>> resultList = heavenLetterService.uploadFiles(files, "heavenLetter");
        return ResponseEntity.ok(resultList);
    }
    //기증자 검색
    @GetMapping("/donorSearch")
    public ResponseEntity<Page<MemorialSearchResponseDto>> searchDonors(
            @ModelAttribute MemorialSearchRequestDto memorialSearchRequest,
            @ModelAttribute CommonPageRequest commonPageRequest
    ) {
        Pageable pageable = commonPageRequest.toPageable("donateDate");
        Page<MemorialSearchResponseDto> result =
                heavenLetterService.searchDonors(memorialSearchRequest, pageable);

        return ResponseEntity.ok(result);
    }

    //꽃 저장
    @PatchMapping("/{letterSeq}/flowers/{flowerSeq}")
    public ResponseEntity<FlowerSelectResponseDto> selectFlower(
            @PathVariable Integer letterSeq,
            @PathVariable Integer flowerSeq,
            @RequestBody @Valid HeavenLetterVerifyRequestDto dto
    ) {
        FlowerSelectResponseDto responseDto = heavenLetterService.selectFlower(letterSeq, flowerSeq, dto.getLetterPasscode());

        return ResponseEntity.ok(responseDto);
    }
}





