package com.ttasum.memorial.service.common;

import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class TurnstileCaptchaVerifier implements CaptchaVerifier {

//    @Value("${turnstile.secret}")
//    private String secret;

    private static final String VERIFY_URL = "https://challenges.cloudflare.com/turnstile/v0/siteverify";

    @Override
    public boolean verifyCaptcha(String token) {
        // TODO: 나중에 실제 검증 로직 활성화
        log.info("개발 모드: 캡차 무조건 통과 처리 중. 토큰: {}", token);
        return true;
    }

    /**
     * Cloudflare Turnstile 토큰 검증
     *
     * @param token 클라이언트가 제출한 captcha 응답 토큰
     * @return 검증 성공 여부 (true = 성공, false = 실패)
     */
//    @Override
//    public boolean verifyCaptcha(String token) {
//        try {
//            RestTemplate restTemplate = new RestTemplate();
//
//            // 요청 바디 구성
//            MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
//            requestBody.add("secret", secret);  // Cloudflare에서 발급한 서버용 키
//            requestBody.add("response", token); // 사용자로부터 받은 토큰
//
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//
//            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);
//
//            // Cloudflare에 검증 요청 전송
//            ResponseEntity<Map> response = restTemplate.postForEntity(VERIFY_URL, requestEntity, Map.class);
//
//            // 응답 데이터에서 "success": true 여부 확인
//            Map body = response.getBody();
//            if (body == null || body.get("success") == null) {
//                log.warn("Turnstile 응답이 null이거나 성공 여부 없음: {}", body);
//                return false;
//            }
//
//            boolean success = Boolean.TRUE.equals(body.get("success"));
//
//            if (!success) {
//                log.warn("Turnstile 검증 실패: {}", response.getBody());
//            }
//
//            return success;
//
//        } catch (Exception e) {
//            log.error("Turnstile 검증 중 오류 발생", e);
//            return false;
//        }
//    }
}
