// AI 호출용 Service 클래스 작성
package com.ttasum.memorial.service.forbiddenWord;

import com.ttasum.memorial.dto.forbiddenWord.ForbiddenResponseDto;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.net.ConnectException;
import java.util.Map;

@Service
public class ForbiddenWordCheckerService {
    private final RestTemplate restTemplate = new RestTemplate();


    public void containsForbiddenWord(String sentence) throws Exception{
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String url = "http://localhost:8001/predict-forbidden";  // AI 서버 주소

            Map<String, String> requestMap = Map.of("sentence", sentence);

            HttpEntity<Map<String, String>> request = new HttpEntity<>(requestMap, headers);
            ResponseEntity<ForbiddenResponseDto> response = restTemplate.postForEntity(url, request, ForbiddenResponseDto.class);

        } catch (ResourceAccessException e) {
            // ConnectException의 래퍼 예외
            Throwable cause = e.getCause();
            if (cause instanceof ConnectException) {
                throw new ConnectException("AI 서버에 연결할 수 없습니다.");
            } else {
                throw e;
            }
        }
    }
}
