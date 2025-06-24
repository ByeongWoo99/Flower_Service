package com.ttasum.memorial.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.beans.factory.annotation.Value;


@Configuration
public class FastApiWebClientConfig {

    /**
     * FastAPI 서버의 기본 URL (application.properties 또는 yml에 fastapi.base-url로 설정)
     */
    @Value("${fastapi.base-url:http://127.0.0.1:8000}")
    private String fastApiBaseUrl;

    /**
     * FastAPI 서버와 통신하기 위한 WebClient 빈
     */
    @Bean("fastApiClient")
    public WebClient fastApiWebClient() {
        return WebClient.builder()
                .baseUrl(fastApiBaseUrl)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}
