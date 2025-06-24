package com.ttasum.memorial.exception.flower;

//추후에 다른 예외도 처리할 수 있도록 Throwable 사용
public class EmotionAnalysisException extends RuntimeException {

    public EmotionAnalysisException(String message) { super (message); }
}
