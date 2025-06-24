package com.ttasum.memorial.controller;

import com.ttasum.memorial.dto.Main.MainDto;
import com.ttasum.memorial.service.Main.MainService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/main")
@RequiredArgsConstructor
public class MainController {

    private final MainService mainService;

    //하늘나라편지, 기증자 추모관 데이터 get
    @GetMapping
    public ResponseEntity<MainDto> getMain() {
        return ResponseEntity.ok(mainService.getMainDto());
    }
}
