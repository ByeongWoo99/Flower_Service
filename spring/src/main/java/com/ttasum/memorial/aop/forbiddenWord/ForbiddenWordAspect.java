//// AOP를 사용해서 특정 서비스 메서드에 자동으로 검사 삽입
//package com.ttasum.memorial.aop.forbiddenWord;
//
//import com.ttasum.memorial.dto.forbiddenWord.ReviewRequestDto;
//import com.ttasum.memorial.exception.forbiddenWord.ForbiddenWordException;
//import com.ttasum.memorial.service.forbiddenWord.ForbiddenWordCheckerService;
//import lombok.RequiredArgsConstructor;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//
//@Aspect
//@Component
//@RequiredArgsConstructor
//public class ForbiddenWordAspect {
//
//    @Autowired
//    private final ForbiddenWordCheckerService checkerService;
//
//    // 이 어노테이션이 붙은 메서드만 API를 거침
//    @Around("@annotation(com.ttasum.memorial.annotation.forbiddenWord.CheckForbiddenWord)")
//    public Object checkForbiddenWord(ProceedingJoinPoint joinPoint) throws Throwable {
//
//        for (Object arg : joinPoint.getArgs()) {
//            // 임시 DTO로 테스트(필드에 reviewText 하나만 존재)
//            if (arg instanceof ReviewRequestDto req) {
//                if (checkerService.containsForbiddenWord(req.getSentence())) {
//                    throw new ForbiddenWordException("금칙어가 포함되어 있습니다.");
//                }
//            }
//        }
//
//        return joinPoint.proceed();
//    }
//}
