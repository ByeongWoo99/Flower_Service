package com.ttasum.memorial.service.blameText;

import com.ttasum.memorial.domain.entity.Comment;
import com.ttasum.memorial.domain.entity.Story;
import com.ttasum.memorial.dto.blameText.BlameResponseDto;
import com.ttasum.memorial.exception.blameText.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;


import java.net.SocketTimeoutException;
import java.util.Map;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class BlameTextCheckerService {
    private final RestTemplate restTemplate = new RestTemplate();
    private BlameTextPersistenceService persistenceService;


    public BlameResponseDto analyzeAndSave(Story story, String boardType) {
        BlameResponseDto response = analyze(story.getContents());
        persistenceService.saveToDb(response, story, boardType); // 트랜잭션 분리됨
        return response;
    }

    public BlameResponseDto analyzeAndSave(Comment comment, String boardType) {
        BlameResponseDto response = analyze(comment.getContents());
        persistenceService.saveToDb(response, comment, boardType); // 트랜잭션 분리됨
        return response;
    }

    public BlameResponseDto analyzeAndUpdate(Story story, String boardType) {
        BlameResponseDto response = analyze(story.getContents());
        persistenceService.updateToDb(response, story, boardType); // 트랜잭션 분리됨
        return response;
    }

    public BlameResponseDto analyzeAndUpdate(Comment comment, String boardType) {
        BlameResponseDto response = analyze(comment.getContents());
        persistenceService.updateToDb(response, comment, boardType); // 트랜잭션 분리됨
        return response;
    }

    public BlameResponseDto analyze(String sentence) {
        String url = "http://localhost:8000/predict-blameText";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        if (sentence == null) {
            throw new MissingSentenceException("비난 텍스트 검사 요청 json에 null 값이 전달 되었습니다.");
        } else if (sentence.isEmpty()) {
            throw new InvalidBlameTextRequestException("비난 텍스트 검사 요청 json에 빈 값이 전달 되었습니다.");
        }

        Map<String, String> requestMap = Map.of("sentence", sentence);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(requestMap, headers);

        try {
            ResponseEntity<BlameResponseDto> response = restTemplate.postForEntity(url, request, BlameResponseDto.class);
            return response.getBody();
        } catch (ResourceAccessException e) {
            if (e.getCause() instanceof SocketTimeoutException) {
                throw new ExternalServerTimeoutException("비난 텍스트 서버 응답이 지연되고 있습니다.");
            }
            throw new ExternalServerConnectionException("비난 텍스트 서버에 연결할 수 없습니다.");
        } catch (HttpClientErrorException e) {
            throw new InvalidBlameTextRequestException("비난 텍스트 요청이 유효하지 않습니다. 요청 형식을 확인하세요.");
        } catch (HttpServerErrorException e) {
            throw new BlameTextApiServerException("비난 텍스트 분석 서버에서 오류가 발생했습니다.");
        }
    }


}
