package com.ttasum.memorial.controller.flower;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ttasum.memorial.dto.common.ApiResponse;
import com.ttasum.memorial.dto.flower.FlowerDto;
import com.ttasum.memorial.dto.flower.FlowerSaveRequestDto;
import com.ttasum.memorial.dto.flower.FlowerUpdateRequestDto;
import com.ttasum.memorial.dto.flower.PageResponseFlowerDto;
import com.ttasum.memorial.service.flower.FlowerAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@CrossOrigin(origins = "http://localhost:8081") //cors 에러 해결 위해 추가
@RestController
@RequestMapping("/admin/flowers")
@RequiredArgsConstructor
public class FlowerAdminController {

    private final FlowerAdminService flowerAdminService;

    //꽃 상세 조회
    @GetMapping("{flowerSeq}")
    public ResponseEntity<FlowerDto> getFlower(@PathVariable Integer flowerSeq) {
        FlowerDto flowerDto = flowerAdminService.findFlower(flowerSeq);
        return ResponseEntity.ok(flowerDto);
    }

    //페이지 꽃 조회
    @GetMapping
    public ResponseEntity<PageResponseFlowerDto> getFlowerPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        PageResponseFlowerDto response = flowerAdminService.findFlowers(pageable);
        return ResponseEntity.ok(response);
    }

    //꽃 생성
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse> createFlower(
            @RequestPart("flower") String flowerJson,
            @RequestPart("imageFile") MultipartFile imageFile
    ) {
        try {
            //꽃 생성 메서드 호출
            FlowerSaveRequestDto requestDto = flowerAdminService.createFlower(flowerJson, imageFile);
            return ResponseEntity.ok(ApiResponse.ok(HttpStatus.CREATED.value(), requestDto.getName() + "꽃이 저장되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.serverError());
        }
    }

    //꽃 정보 수정(이미지 포함)
    @PatchMapping(value = "/{flowerSeq}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse> updateFlowerWithImage(
            @PathVariable Integer flowerSeq,
            @RequestPart("flower") String flowerJson,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile
    ) {
        try {
            //JSON 문자열 Java객체로 변환(역직렬화)
            ObjectMapper objectMapper = new ObjectMapper();
            FlowerUpdateRequestDto requestDto = objectMapper.readValue(flowerJson, FlowerUpdateRequestDto.class);


            flowerAdminService.updateFlowerWithImage(flowerSeq, requestDto, imageFile);

            return ResponseEntity.ok(ApiResponse.ok(HttpStatus.OK.value(), flowerSeq + "번 꽃 수정 완료"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.serverError());
        }
    }

    // 꽃 정보 수정(텍스트만)
    @PatchMapping(value = "/{flowerSeq}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse> updateFlower(
            @RequestBody @Valid FlowerUpdateRequestDto requestDto,
            @PathVariable Integer flowerSeq) {

        flowerAdminService.updateFlower(flowerSeq, requestDto);

        return ResponseEntity.ok(ApiResponse.ok(HttpStatus.OK.value(), flowerSeq + "번 수정 완료"));
    }

    //꽃 삭제 (delFlag 활성화("Y"))
    @DeleteMapping("/{flowerSeq}")
    public ResponseEntity<ApiResponse> deleteFlower(@PathVariable Integer flowerSeq) {
       flowerAdminService.deleteFlower(flowerSeq);

        return ResponseEntity.ok(ApiResponse.ok(HttpStatus.OK.value(), flowerSeq + "가 삭제 되었습니다."));
    }

}
