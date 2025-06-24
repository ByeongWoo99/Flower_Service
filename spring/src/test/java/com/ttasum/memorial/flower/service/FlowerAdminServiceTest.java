package com.ttasum.memorial.flower.service;


import com.ttasum.memorial.domain.entity.flower.Flower;
import com.ttasum.memorial.domain.repository.flower.FlowerRepository;
import com.ttasum.memorial.dto.flower.FlowerDto;
import com.ttasum.memorial.dto.flower.FlowerSaveRequestDto;
import com.ttasum.memorial.dto.flower.FlowerUpdateRequestDto;
import com.ttasum.memorial.dto.flower.PageResponseFlowerDto;
import com.ttasum.memorial.exception.flower.FlowerAlreadyDeleted;
import com.ttasum.memorial.exception.flower.FlowerNotFoundException;
import com.ttasum.memorial.service.flower.FlowerAdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
public class FlowerAdminServiceTest {

    @Mock
    private FlowerRepository flowerRepo;

    @Mock
    private S3Client s3Client;

    @InjectMocks
    private FlowerAdminService flowerAdminService;

    private Flower flower;

    @BeforeEach
    void setUp() {
        //꽃 생성
         flower = Flower.builder()
                .seq(1)
                .name("테스트")
                .emotion("감정")
                .meaning("꽃말")
                .imgUrl("이미지 주소.jpg")
                .orgFileName("파일 이름.jpg")
                .delFlag("N")
                .build();

         //일부 테스트에서만 사용해서 lenient 적용 , 느스한 스텁
        lenient().when(flowerRepo.findById(1))
                .thenReturn(Optional.of(flower));
    }


    @DisplayName("꽃 상세 조회 테스트")
    @Test
    void findFlower() {
        // when
        // 꽃 조회 메서드 호출
        FlowerDto dto = flowerAdminService.findFlower(1);

        // then
        assertNotNull(dto);
        assertEquals(1, dto.getSeq());
        assertEquals("테스트", dto.getName());
    }

    @DisplayName("꽃 조회 시 해당 꽃 없을 때 예외 테스트")
    @Test
    void findFlower_FlowerNotFound() {
        given(flowerRepo.findById(999)).willReturn(Optional.empty());

        assertThrows(
                FlowerNotFoundException.class,
                () -> flowerAdminService.findFlower(999));
    }

    @DisplayName("꽃 목록 조회 테스트")
    @Test
    void findFlowers() {
        //0번째 페이지, 페이지 당 꽃 개수 3개
        Pageable pageReq = PageRequest.of(0, 3, Sort.by("seq"));
        Flower flower1 = Flower.builder().seq(2).name("A").imgUrl("u1").orgFileName("o1").delFlag("N").build();
        Flower flower2 = Flower.builder().seq(3).name("B").imgUrl("u2").orgFileName("o2").delFlag("N").build();

        //페이지에 담긴 실제 요소, 페이지 정보, 총 개수
        Page<Flower> flowerPage = new PageImpl<>(List.of(flower1, flower2), pageReq, 2);
        given(flowerRepo.findAll(pageReq)).willReturn(flowerPage);

        PageResponseFlowerDto res = flowerAdminService.findFlowers(pageReq);

        assertThat(res.getFlowers()).hasSize(2);
        assertThat(res.getPage()).isEqualTo(0);
        assertThat(res.getSize()).isEqualTo(3);
        assertThat(res.getTotalElements()).isEqualTo(2);
        assertThat(res.getTotalPages()).isEqualTo(1);
        assertThat(res.isLastPage()).isTrue();
        assertThat(res.getFlowers().get(0).getName()).isEqualTo("A");
    }

    @DisplayName("꽃 생성 테스트")
    @Test
    void createFlower() throws IOException {
        // JSON형태의 꽃 정보 생성
        String flowerJson = """
                {
                "name" : "테스트",
                "emotion" : "감정",
                "meaning" : "꽃말"
                }
                """;

        //이미지 정보 생성
        MultipartFile testImage = mock(MultipartFile.class);
        when(testImage.isEmpty()).thenReturn(false);
        when(testImage.getOriginalFilename()).thenReturn("파일 이름.jpg");
        when(testImage.getContentType()).thenReturn("image/jpg");
        InputStream is = new ByteArrayInputStream(new byte[]{1,2,3});
        when(testImage.getInputStream()).thenReturn(is);
        when(testImage.getSize()).thenReturn(3L);

        //실제 S3 서버 호출 대신, 비어있는 PutObjectResponse 반환
        when(s3Client.putObject(
                any(PutObjectRequest.class),
                any(RequestBody.class))
        )
                .thenReturn(PutObjectResponse.builder().build());

        when(flowerRepo.save(any(Flower.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        //꽃 생성 메서드 호출
        FlowerSaveRequestDto requestDto = flowerAdminService.createFlower(flowerJson, testImage);

        assertEquals("테스트", requestDto.getName());
        assertEquals("감정", requestDto.getEmotion());
        assertEquals("꽃말", requestDto.getMeaning());
        assertEquals("파일 이름.jpg",  requestDto.getOrgFileName());

    }

    @DisplayName("꽃 수정 테스트, 이미지 제외")
    @Test
    void updateFlower() {

        //수정 요청 Dto 객체 생성
        FlowerUpdateRequestDto requestDto = new FlowerUpdateRequestDto();

        //수정할 값 설정
        requestDto.setName("수정");
        requestDto.setEmotion("수정");
        requestDto.setMeaning("수정 입니다");

        //수정 메서드 호출
        flowerAdminService.updateFlower(1, requestDto);

        assertEquals("수정", flower.getName());
        assertEquals("수정", flower.getEmotion());
        assertEquals("수정 입니다", flower.getMeaning());
    }

    @DisplayName("꽃 수정 테스트, 이미지 포함")
    @Test
    void updateFlowerWithImage() throws IOException {
        // 테스트용 MultipartFile
        MultipartFile testImage = mock(MultipartFile.class);

        when(testImage.isEmpty()).thenReturn(false);

        // spy 로 service 가져와서 saveImage만 스텁
        FlowerAdminService spyService = spy(flowerAdminService);
        Map<String, String> fakeInfo = Map.of(
                "imageUrl", "새로운 이미지 주소.jpg",
                "originalFileName", "새로운 파일 이름.jpg"
        );
        //
        doReturn(fakeInfo).when(spyService).saveImage(testImage);

        // 수정 요청 DTO 생성
        FlowerUpdateRequestDto req = new FlowerUpdateRequestDto();
        req.setName("수정");
        req.setEmotion("수정");
        req.setMeaning("수정");

        // when
        // 수정 메서드 호출
        spyService.updateFlowerWithImage(1, req, testImage);

        // then
        assertEquals("수정", flower.getName());
        assertEquals("수정", flower.getEmotion());
        assertEquals("수정", flower.getMeaning());
        assertEquals("새로운 이미지 주소.jpg", flower.getImgUrl());
        assertEquals("새로운 파일 이름.jpg", flower.getOrgFileName());
    }

    @DisplayName("꽃 삭제 테스트, delFlag 'N' -> 'Y'")
    @Test
    void deleteFlower() {

        //삭제 메서드 호출
        flowerAdminService.deleteFlower(1);

        assertThat(flower.getDelFlag()).isEqualTo("Y");
    }

    @DisplayName("이미 삭제한 꽃 삭제시 예외 테스트")
    @Test
    void deleteFlower_FlowerAlreadyDeleted() {
        //delFlag 활성화
        flower.setDelFlag("Y");

        assertThrows(FlowerAlreadyDeleted.class, () -> flowerAdminService.deleteFlower(1));
    }

    @DisplayName("이미지 저장 테스트")
    @Test
    void saveImage() throws IOException {
        // bucket, regionName 설정
        ReflectionTestUtils.setField(flowerAdminService, "bucket", "my-bucket");
        ReflectionTestUtils.setField(flowerAdminService, "regionName", "us-east-1");

        // MultipartFile 모킹
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getOriginalFilename()).thenReturn("파일 이름.jpg");
        when(file.getContentType()).thenReturn("이미지 주소.jpg");
        InputStream is = new ByteArrayInputStream(new byte[]{10,20,30});
        when(file.getInputStream()).thenReturn(is);
        when(file.getSize()).thenReturn(3L);

        // S3Client.putObject 스텁: 빈 응답 반환
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());


        Map<String, String> result = flowerAdminService.saveImage(file);


        // originalFileName
        assertThat(result.get("originalFileName")).isEqualTo("파일 이름.jpg");

        // imageUrl 포맷 : https://my-bucket.s3.us-east-1.amazonaws.com/flowers/{uuid}.jpg
        String url = result.get("imageUrl");
        assertThat(url)
                .startsWith("https://my-bucket.s3.us-east-1.amazonaws.com/flowers/")
                .endsWith(".jpg");

        // S3 업로드 호출 검증
        verify(s3Client, times(1))
                .putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

}
