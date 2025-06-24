// 샘플 API 컨트롤러 클래스
package com.ttasum.memorial.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    // 단순 메시지 반환 테스트 API
    @GetMapping("/hello")
    public String hello() {
        return "Hello, Spring Boot!";
    }
}
