package com.ttasum.memorial.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConvertGender {
    /** M → 남, W/F → 여, 나머지 → defaultValue */
    String male() default "남";
    String female() default "여";
    String defaultValue() default "-";
}
