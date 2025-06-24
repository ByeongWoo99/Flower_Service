package com.ttasum.memorial.service.flower;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ttasum.memorial.domain.entity.flower.Flower;
import com.ttasum.memorial.domain.repository.flower.FlowerRepository;
import com.ttasum.memorial.dto.flower.FlowerDto;
import com.ttasum.memorial.dto.flower.FlowerSaveRequestDto;
import com.ttasum.memorial.dto.flower.FlowerUpdateRequestDto;
import com.ttasum.memorial.dto.flower.PageResponseFlowerDto;
import com.ttasum.memorial.exception.flower.FlowerAlreadyDeleted;
import com.ttasum.memorial.exception.flower.FlowerNotFoundException;
import com.ttasum.memorial.exception.flower.ImageEmptyException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import org.springframework.beans.factory.annotation.Value;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;


import java.io.IOException;

import java.util.*;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FlowerAdminService {

    private final FlowerRepository flowerRepo;

    private final S3Client s3Client;

    //aws region 정보
    @Value("${cloud.aws.region.static}")
    private String regionName;

    //버킷 정보
    @Value("${app.s3.bucket}")
    private String bucket;

//    //꽃 사진 저장 경로 설정, 현재 디렉토리가 DBE_Final_Project/spring로 설정되어 있어야 함.
//    private static final String UPLOAD_DIR = "src/main/resources/static/images/flower/";

    //꽃 한 개 찾기 메서드
    @Transactional(readOnly = true)
    public FlowerDto findFlower(Integer flowerSeq) {
        Optional<Flower> flower = flowerRepo.findById(flowerSeq);

        if (flower.isEmpty()) {
            throw new FlowerNotFoundException("해당하는 꽃을 찾을 수 없습니다");
        } else {
            return FlowerDto.fromEntity(flower.get());
        }
    }

    //꽃 목록 조회 메서드
    @Transactional(readOnly = true)
    public PageResponseFlowerDto findFlowers(Pageable pageable) {
        Page <Flower> flowers = flowerRepo.findAll(pageable);

        List<FlowerDto> flowerDtos = flowers.getContent().stream()
                .map(FlowerDto::fromEntity)
                .toList();

        return new  PageResponseFlowerDto(
                flowerDtos,
                flowers.getNumber(), //현재 페이지 번호
                flowers.getSize(),  //한 페이지에 들어가는 데이터 개수
                flowers.getTotalElements(),
                flowers.getTotalPages(),
                flowers.isLast()
        );
    }

    //꽃 저장 메서드
    @Transactional
    public FlowerSaveRequestDto createFlower(String flowerJson, MultipartFile imageFile) throws IOException {
        // JSON 문자열을 DTO 변환
        ObjectMapper objectMapper = new ObjectMapper();
        FlowerSaveRequestDto requestDto = objectMapper.readValue(flowerJson, FlowerSaveRequestDto.class);

        // 이미지 저장 및 정보 세팅
        Map<String, String> imageInfo = saveImage(imageFile);
        requestDto.setImgUrl(imageInfo.get("imageUrl"));
        requestDto.setOrgFileName(imageInfo.get("originalFileName"));

        // 저장 후 반환
        flowerRepo.save(requestDto.toEntity());
        return requestDto;
    }

    //꽃 수정 메서드, @Transactional이용해서 메서드 실행 끝나면 자동으로 수정한 값 DB저장
    @Transactional
    public void updateFlower(Integer flowerSeq, FlowerUpdateRequestDto requestDto) {
        Flower flower = flowerRepo.findById(flowerSeq)
                .orElseThrow(() -> new FlowerNotFoundException("해당하는 꽃을 찾을 수 없습니다"));

        // 이미지 URL이 null인 경우 기존 이미지 유지
        if (requestDto.getImgUrl() == null) {
            requestDto.setImgUrl(flower.getImgUrl());
            requestDto.setOrgFileName(flower.getOrgFileName());
        }

        flower.updateFlower(requestDto);
    }

    //꽃 수정 메서드(이미지 포함)
    @Transactional
    public void updateFlowerWithImage(Integer flowerSeq, FlowerUpdateRequestDto requestDto, MultipartFile imageFile)  {
        if (imageFile != null && !imageFile.isEmpty()) {
            Map<String, String> imageInfo = saveImage(imageFile);
            requestDto.setImgUrl(imageInfo.get("imageUrl"));
            requestDto.setOrgFileName(imageInfo.get("originalFileName"));
        }

        updateFlower(flowerSeq, requestDto); // 기존 로직 호출
    }

    //꽃 삭제(delFlag 활성화) 메서드
    @Transactional
    public void deleteFlower(Integer flowerSeq) {
        Flower flower = flowerRepo.findById(flowerSeq)
                .orElseThrow(() -> new FlowerNotFoundException("해당하는 꽃을 찾을 수 없습니다."));

        //이미 delFlag 활성화 시 에러 처리
        if (flower.getDelFlag().equals("Y")) {
            throw new FlowerAlreadyDeleted("이미 삭제된 꽃 입니다.");
        }

        flower.setDelFlag("Y");
    }

//    //이미지 저장 static 폴더
//    public Map<String, String> saveImage(MultipartFile imageFile) throws IOException {
//        if (imageFile.isEmpty()) {
//            throw new ImageEmptyException("이미지는 필수입니다.");
//        }
//
//        //원본 파일명
//        String originalFileName = imageFile.getOriginalFilename();
//        //확장자 추출
//        String ext = originalFileName.substring(originalFileName.lastIndexOf("."));
//        //uuid이용한 파일명
//        String savedFileName = UUID.randomUUID().toString() + ext;
//
//        //폴더 없을 시 생성
//        File dir = new File(UPLOAD_DIR);
//        if (!dir.exists()) dir.mkdirs();
//
//        //업로드 한 파일 읽어서 지정한 경로에 저장
//        Path path = Paths.get(UPLOAD_DIR, savedFileName);
//        try (InputStream in = imageFile.getInputStream()) {
//            Files.copy(in, path, StandardCopyOption.REPLACE_EXISTING);
//        }
//
//        Map<String, String> result = new HashMap<>();
//        result.put("imageUrl", "/images/flower/" + savedFileName);
//        result.put("originalFileName", originalFileName);
//        return result;
//    }

    public Map<String, String> saveImage(MultipartFile imageFile) {
        if (imageFile.isEmpty()) {
            throw new ImageEmptyException("이미지를 첨부해주세요.");
        }

        //원본 파일명
        String original = imageFile.getOriginalFilename();
        //확장자 추출
        String ext = original.substring(original.lastIndexOf('.'));
        //uuid이용하여 flowers/ 폴더안에 파일 저장
        String key = "flowers/" + UUID.randomUUID() + ext;

        //S3 업로드 요청
        try {
            PutObjectRequest req = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(imageFile.getContentType())
                    .build();

            //파일 읽은 후, .putObject 호출하여 업로드
            s3Client.putObject(req,
                    RequestBody.fromInputStream(imageFile.getInputStream(), imageFile.getSize()));

            //브라우저에서 바로 접근 가능한 url 생성(객체 키, 버킷, 지역이름 조합)
            String url = String.format("https://%s.s3.%s.amazonaws.com/%s",
                    bucket, regionName, key);

            return Map.of(
                    "imageUrl", url,
                    "originalFileName", original
            );
        } catch (IOException e) {
            throw new RuntimeException("이미지 업로드 실패", e);
        }
    }

}
