package com.ttasum.memorial.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FormatDate {
    // 입력값 "yyyyMMdd" 형태일 떄, 출력 패턴 지정
    String pattern() default "yyyyMMdd";
    // 출력 포맷 -> yyyy. MM. dd
    String output() default "yyyy. MM. dd";
}
