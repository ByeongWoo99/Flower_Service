/**
 * logging aop ( 컨트롤러에서 log 분리)
 * 현재 컨트롤러에 들어오는 모든 매서드 요청에 대해 before로 로그 남기기.
 * 로그는 요청 url , method type, ip, content 4가지로 우선 남기기.
 */
package com.ttasum.memorial.aop.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
@Slf4j
public class ControllerLoggingAspect {

    @Pointcut("execution(public * com.ttasum.memorial.controller..*(..))")
    public void controllerMethods() {}

    @Before("controllerMethods()")
    public void logRequest(JoinPoint joinPoint) {
        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();


        String method = request.getMethod();        // GET, POST ....
        String uri = request.getRequestURI();       // 요청 URL
        String handler = joinPoint.getSignature().toShortString();  // 클래스명.메서드명()
        String clientIp = request.getRemoteAddr(); // client ip addr -> 프록시나 로드밸런서 사용하게 되면 변경 해야함

        log.info("Type={} - request={} - clientIp=[{}] methodCall={}", method, uri, clientIp,handler);

    }

}
