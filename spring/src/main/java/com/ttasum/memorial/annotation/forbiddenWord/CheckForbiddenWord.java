// Spring AOP에서 @annotation 방식으로 금칙어 체크를 처리
package com.ttasum.memorial.annotation.forbiddenWord;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CheckForbiddenWord {
}
