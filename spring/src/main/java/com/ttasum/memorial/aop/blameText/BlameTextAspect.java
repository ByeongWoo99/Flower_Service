package com.ttasum.memorial.aop.blameText;

import com.ttasum.memorial.domain.entity.Comment;
import com.ttasum.memorial.domain.entity.donationStory.DonationStoryComment;
import com.ttasum.memorial.domain.entity.Story;
import com.ttasum.memorial.dto.blameText.BlameResponseDto;
import com.ttasum.memorial.exception.blameText.BlameTextException;
import com.ttasum.memorial.service.blameText.BlameTextCheckerService;
import com.ttasum.memorial.service.forbiddenWord.ForbiddenWordCheckerService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * 비난 텍스트를 사전에 분석하기 위한 AOP 클래스.
 * 특정 메서드 실행 전후에 비난 문장 여부를 검사하는 횡단 관심사를 분리함.
 */
@Aspect  // AOP 기능을 위한 클래스임을 명시 (횡단 관심사를 모듈화)
@Component  // Spring 컴포넌트로 등록
@RequiredArgsConstructor  // final 필드에 대한 생성자 자동 생성 (생성자 주입)
public class BlameTextAspect {

    public final BlameTextCheckerService checkerService;
    private final ForbiddenWordCheckerService forbiddenWordCheckerService;

    /**
     * Pointcut: 어노테이션이 붙은 메서드 실행 시점
     * Advice: 실행 전후로 비난 문장 여부를 검사하는 로직 수행
     **/
    // 어노테이션이 붙은 메서드 실행 전후로 동작
    @Around("@annotation(com.ttasum.memorial.annotation.blameText.CheckBlameText)")
    public Object checkBlameText(ProceedingJoinPoint joinPoint) throws Throwable {
        boolean isCreated = false;
        String boardType = "";

        for (Object arg : joinPoint.getArgs()) {
            if(arg instanceof Boolean)
                isCreated = (Boolean) arg;
            if(arg instanceof String)
                boardType = (String) arg;
        }
        BlameResponseDto response;
        System.out.println("boardType = " + boardType);

        // 메서드의 인자 중 Story 타입이 있는지 확인
        for (Object arg : joinPoint.getArgs()) {
            // 게시글
            if(arg instanceof Story req) {
                // 문장을 분석하고 결과를 가져옴
                if(isCreated) {
                    response = checkerService.analyzeAndSave(req, boardType);
                }else{
                    response = checkerService.analyzeAndUpdate(req, boardType);
                }

                // 비난 의도로 판단되면 예외 발생시켜 흐름 중단
                if (response.getLabel() == 1) {
                    // 금칙어 체크가 필요한 경우 아래 주석을 풀면 됨
                    // forbiddenWordCheckerService.containsForbiddenWord(req.getSentence());
                    // comment db 만들고 진행 예정
                    throw new BlameTextException("비난하는 의도가 예상되는 글입니다. 관리자가 해당 글을 삭제할 수 있습니다.");
                }
            }

            // 댓글
            if(arg instanceof Comment comment) {
                if(isCreated) {
                    response = checkerService.analyzeAndSave(comment, boardType);
                }else {
                    response = checkerService.analyzeAndUpdate(comment, boardType);
                }

                // 비난 글 판단
                if(response.getLabel() == 1) {
                    throw new BlameTextException("비난하는 의도가 예상되는 댓글입니다. 관리자가 해당 댓글을 삭제할 수 있습니다.");
                }
            }
        }

        // 문제가 없으면 원래 메서드 실행 계속 진행
        return joinPoint.proceed();
    }
}
