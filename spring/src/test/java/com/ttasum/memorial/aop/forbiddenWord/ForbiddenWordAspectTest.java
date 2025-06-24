//package com.ttasum.memorial.aop.forbiddenWord;
//
//import com.ttasum.memorial.dto.forbiddenWord.ReviewRequestDto;
//import com.ttasum.memorial.exception.forbiddenWord.ForbiddenWordException;
//import com.ttasum.memorial.service.forbiddenWord.ForbiddenWordCheckerServiceImpl;
//import com.ttasum.memorial.service.forbiddenWord.TestReviewService;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.SpyBean;
//import org.springframework.context.annotation.Import;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.doReturn;
//import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
//
//@SpringBootTest
//@Import({ForbiddenWordAspect.class, ForbiddenWordCheckerServiceImpl.class, TestReviewService.class})
//class ForbiddenWordAspectTest {
//
//    @Autowired
//    private TestReviewService reviewService;
//
//    @SpyBean
//    private ForbiddenWordCheckerServiceImpl checkerService;
//
//    @Test
//    void 금칙어_포함시_예외발생() {
//        // given
//        ReviewRequestDto request = new ReviewRequestDto();
//        request.setReviewText("이거 금지어 포함");
//
//        // checkerService가 실제 Bean이므로, SpyBean으로 감싸고 mocking
//        doReturn(true).when(checkerService).containsForbiddenWord("이거 금지어 포함");
//
//        // when & then
//        assertThrows(ForbiddenWordException.class, () -> {
//            reviewService.saveReview(request);
//        });
//    }
//
//    @Test
//    void 금칙어_없을시_정상처리() {
//        ReviewRequestDto request = new ReviewRequestDto();
//        request.setReviewText("이거는 정상적인 리뷰입니다");
//
//        doReturn(false).when(checkerService).containsForbiddenWord("이거는 정상적인 리뷰입니다");
//
//        assertDoesNotThrow(() -> {
//            reviewService.saveReview(request);
//        });
//    }
//}