package com.ttasum.memorial.flower.service;

import com.ttasum.memorial.domain.entity.flower.Flower;
import com.ttasum.memorial.domain.repository.flower.FlowerRepository;
import com.ttasum.memorial.dto.flower.FlowerDto;
import com.ttasum.memorial.exception.flower.EmotionEmptyException;
import com.ttasum.memorial.exception.flower.FlowerNotFoundException;
import com.ttasum.memorial.service.flower.FastApiClientService;
import com.ttasum.memorial.service.flower.FlowerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FlowerServiceTest {

    @Mock
    FastApiClientService fastApiClientService;

    @Mock
    FlowerRepository flowerRepo;

    @InjectMocks
    FlowerService flowerService;

    @DisplayName("감정에 해당하는 꽃 추천")
    @Test
    public void recommendFlowersByContent() {
        // given
        // FastAPI 모킹: 두 개의 감정 반환
        List<String> mockEmotions = Arrays.asList("기쁨", "슬픔");
        when(fastApiClientService.getEmotions(anyString()))
                .thenReturn(mockEmotions);

        // "기쁨" → 후보 하나
        Flower joyFlower = Flower.builder()
                .seq(1)
                .name("해바라기")
                .emotion("기쁨")
                .meaning("기쁜 마음")
                .build();

        when(flowerRepo.findByEmotionAndDelFlag("기쁨", "N"))
                .thenReturn(Collections.singletonList(joyFlower));

        // "슬픔" → 후보 두 개
        Flower sadFlower1 = Flower.builder()
                .seq(2)
                .name("백합")
                .emotion("슬픔")
                .meaning("슬픈 마음")
                .build();

        Flower sadFlower2 = Flower.builder()
                .seq(3)
                .name("국화")
                .emotion("슬픔")
                .meaning("슬픈 마음2")
                .build();

        when(flowerRepo.findByEmotionAndDelFlag("슬픔", "N"))
                .thenReturn(Arrays.asList(sadFlower1, sadFlower2));
        // when
        // 실제 서비스 호출
        List<FlowerDto> result = flowerService.recommendFlowersByContent("아무 내용");

        // then
        // 감정 개수(2) 만큼 꽃이 추천되어야 한다.
        assertEquals(2, result.size(),
                "감정이 2개여서 최대 꽃 리스트 크기가 2여야 한다.");

        // 첫 번째 인덱스는 joyFlower가 반드시 포함
        assertTrue(
                result.stream()
                        .anyMatch(f -> "기쁨".equals(f.getEmotion())
                                && "해바라기".equals(f.getName())),
                "첫 번째 추천 꽃은 반드시 '해바라기'(기쁨)여야 한다."
        );

        // 두 번째 인덱스는 sadFlower1 또는 sadFlower2 중 하나
        boolean hasSad = result.stream()
                .anyMatch(f -> "슬픔".equals(f.getEmotion())
                        && ("백합".equals(f.getName()) || "국화".equals(f.getName())));
        assertTrue(hasSad, "두 번째 추천 꽃은 '백합' 또는 '국화'(슬픔) 중 하나여야 한다.");
    }


    @DisplayName("감정에 해당하는 추천 꽃 없을 때")
    @Test
    public void recommendFlowersByContent_FlowerNotFound() {
        // given
        when(fastApiClientService.getEmotions(anyString()))
                .thenReturn(Collections.singletonList("그리움"));

        // when
        // 그리움과 일치하는 꽃 없음
        when(flowerRepo.findByEmotionAndDelFlag("그리움", "N"))
                .thenReturn(Collections.emptyList());

        // then
        // FlowerNotFound 예외 검증
        assertThrows(
                FlowerNotFoundException.class,
                () -> flowerService.recommendFlowersByContent("아무 내용"));
    }

    @DisplayName("빈 감정이 반환됐을 때, FlowerService에서 예외 전파 테스트")
    @Test
    void recommendFlowersByContent_EmotionEmpty() {
        //FastAPI서버에서 빈 감정 반환
        when(fastApiClientService.getEmotions(anyString()))
                .thenThrow(new EmotionEmptyException("감정을 찾을 수 없습니다."));

        assertThrows(EmotionEmptyException.class,
                () -> flowerService.recommendFlowersByContent("아무 내용"));
    }
}
