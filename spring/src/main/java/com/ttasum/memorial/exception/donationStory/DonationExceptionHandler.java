package com.ttasum.memorial.exception.donationStory;

import com.ttasum.memorial.dto.common.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.ttasum.memorial.controller.donation")
@Slf4j
public class DonationExceptionHandler {

    @ExceptionHandler(DonationStoryNotFoundException.class)
    public ResponseEntity<ApiResponse> handleDonationStoryNotFound(DonationStoryNotFoundException ex) {
        log.info("Donation 예외 발생:{}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.fail(HttpStatus.NOT_FOUND.value(), ex.getMessage()));
    }
}
