package com.ttasum.memorial.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 또는 특정 경로
                .allowedOrigins("http://localhost:5173", "http://koda.elementsoft.biz:8080")
                // 프론트 주소
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS") // 꼭 PATCH, OPTIONS 포함
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
