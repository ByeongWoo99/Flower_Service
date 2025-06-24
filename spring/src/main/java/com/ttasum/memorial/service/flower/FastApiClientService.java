package com.ttasum.memorial.service.flower;

import com.ttasum.memorial.dto.flower.FastApiResponseDto;
import com.ttasum.memorial.exception.flower.EmotionAnalysisException;
import com.ttasum.memorial.exception.flower.ContentEmptyException;
import com.ttasum.memorial.exception.flower.EmotionEmptyException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FastApiClientService {

    @Qualifier("fastApiClient")
    private final WebClient fastApiClient;

    /**
     * FastAPI 서버로 content를 보내서 감정 리스트(List<String>)를 받아오는 메서드
     */
    public List<String> getEmotions(String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new ContentEmptyException("빈 내용이 전달되었습니다.");
        }

        FastApiResponseDto responseDto;
        try {
            // 직접 fastApiClient를 사용해 POST 요청
            responseDto = fastApiClient.post()
                    .uri("/classify")
                    .bodyValue(Map.of("content", content))
                    .retrieve()
                    .bodyToMono(FastApiResponseDto.class)
                    .block();
        } catch (WebClientException e) {
            throw new EmotionAnalysisException("감정 분석 서버에 연결할 수 없습니다.");
        }

        if (responseDto == null || responseDto.getEmotions() == null || responseDto.getEmotions().isEmpty()) {
            throw new EmotionEmptyException("감정을 찾을 수 없습니다.");
        }
        return responseDto.getEmotions();
    }

}
