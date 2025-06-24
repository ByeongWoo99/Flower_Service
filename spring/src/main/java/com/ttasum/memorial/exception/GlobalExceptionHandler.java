package com.ttasum.memorial.exception;


import com.ttasum.memorial.dto.common.ApiResponse;
import com.ttasum.memorial.exception.common.badRequest.BadRequestException;
import com.ttasum.memorial.exception.flower.*;
import com.ttasum.memorial.exception.forbiddenWord.ForbiddenWordException;
import com.ttasum.memorial.exception.heavenLetter.*;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.ttasum.memorial.dto.heavenLetter.response.HeavenLetterResponseDto;
import com.ttasum.memorial.exception.common.conflict.AlreadyDeletedException;
import com.ttasum.memorial.exception.common.notFound.NotFoundException;
import lombok.extern.slf4j.Slf4j;

import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    // 잘못된 검색 필드 등 BadRequest 계열
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse> handleBadRequest(BadRequestException ex) {
        log.info("잘못된 요청: {}", ex.getMessage());
        return ResponseEntity.badRequest()
                .body(ApiResponse.badRequest(ex.getMessage()));
    }

    // 유효성 검사 실패 (400 Bad Request)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleValidation(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        if (msg.isEmpty()) {
            msg = "필수 입력값이 누락되었습니다.";
        }
        ApiResponse response = ApiResponse.badRequest(msg);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiResponse> handleNotFound(NotFoundException ex) {
        log.info("리소스를 찾을 수 없음: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.notFound(ex.getMessage()));
    }

    // ResponseStatusException 처리
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiResponse> handleStatusException(ResponseStatusException ex) {
        ApiResponse response = ApiResponse.fail(ex.getStatus().value(), ex.getReason());
        return ResponseEntity.status(ex.getStatus()).body(response);
    }

    //유효성 검증 실패(400)
    //@ExceptionHandler : 지정한 예외가 발생했을 때 메서드 자동 호출
    //ResponseEntity<CommonResponse<Void>> : 응답 객체 형식
    //CommonResponse<Void>: 우리가 만든 공통 응답 구조. Void는 data가 없다는 뜻
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<com.ttasum.memorial.dto.heavenLetter.response.HeavenLetterResponse> handleValidationException(MethodArgumentNotValidException e){
//        String message = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
//        return ResponseEntity
//                .status(HttpStatus.BAD_REQUEST)
//                .body(com.ttasum.memorial.dto.heavenLetter.response.HeavenLetterResponse.fail(400,message));
//    }
    //잘못된 값 전달(비밀번호)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<HeavenLetterResponseDto> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(HeavenLetterResponseDto.fail(400, e.getMessage()));
    }
    // 이미 삭제된 리소스 요청 (409 Conflict)
    @ExceptionHandler(AlreadyDeletedException.class)
    public ResponseEntity<ApiResponse> handleAlreadyDeleted(AlreadyDeletedException ex) {
        log.info("이미 삭제된 리소스 요청: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.conflict(ex.getMessage()));
    }

    //감정 분석 서버 통신 에러 예외 처리
    @ExceptionHandler(EmotionAnalysisException.class)
    public ResponseEntity<?> handleEmotionAnalysisException(EmotionAnalysisException e) {
        return ResponseEntity.badRequest().body(
                ApiResponse.badRequest(e.getMessage())
        );
    }

    //빈 감정 반환 했을 때 예외 처리
    @ExceptionHandler(EmotionEmptyException.class)
    public ResponseEntity<?> handleEmptyEmotionException(EmotionEmptyException e) {
        return ResponseEntity.badRequest().body(
                ApiResponse.badRequest(e.getMessage())
        );
    }

    //꽃 못 찾았을 때 예외 처리
    @ExceptionHandler(FlowerNotFoundException.class)
    public ResponseEntity<?> handleFlowerNotFoundException(FlowerNotFoundException e) {
        return ResponseEntity.badRequest().body(
                ApiResponse.badRequest(e.getMessage())
        );
    }

    //꽃 삭제 시 예외 처리
    @ExceptionHandler(FlowerAlreadyDeleted.class)
    public ResponseEntity<?> handleFlowerAlreadyDeleted(FlowerAlreadyDeleted e) {
        return ResponseEntity.badRequest().body(
                ApiResponse.badRequest(e.getMessage())
        );
    }

    //꽃 이미지 없을 시 예외 처리
    @ExceptionHandler(ImageEmptyException.class)
    public ResponseEntity<?> handleImageEmptyException(ImageEmptyException e) {
        return ResponseEntity.badRequest().body(
                ApiResponse.badRequest(e.getMessage())
        );
    }

     // 서버 내부 오류 (500 Internal Server Error)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleAll(Exception ex) {
        log.error("서버 내부 오류", ex);
        ApiResponse response = ApiResponse.serverError();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

}
