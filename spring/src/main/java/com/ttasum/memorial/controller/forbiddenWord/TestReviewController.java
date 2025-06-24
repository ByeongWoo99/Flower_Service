package com.ttasum.memorial.controller.forbiddenWord;

import com.ttasum.memorial.dto.forbiddenWord.ReviewRequestDto;
import com.ttasum.memorial.service.forbiddenWord.TestReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class TestReviewController {

    private final TestReviewService reviewService;

    public TestReviewController(TestReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/reviews")
    public ResponseEntity<?> saveReview(@RequestBody ReviewRequestDto request) {
        return reviewService.saveReview(request);
    }
}
