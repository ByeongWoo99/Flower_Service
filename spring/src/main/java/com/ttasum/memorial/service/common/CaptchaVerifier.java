package com.ttasum.memorial.service.common;

public interface CaptchaVerifier {
    /**
     * 캡차 토큰을 검증한다.
     * @param token 클라이언트로부터 전달받은 토큰
     * @return 성공 여부 (true: 성공, false: 실패)
     */
    boolean verifyCaptcha(String token);
}
