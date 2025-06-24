// 날짜/시간 관련 유틸리티 기능을 제공하는 클래스
package com.ttasum.memorial.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {

    // 현재 시간을 "yyyy-MM-dd HH:mm:ss" 형식으로 반환
    public static String now() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}

