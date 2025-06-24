//package com.ttasum.memorial.aop.forbiddenWord;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.ttasum.memorial.dto.forbiddenWord.ReviewRequestDto;
//import com.ttasum.memorial.service.forbiddenWord.ForbiddenWordCheckerServiceImpl;
//import com.ttasum.memorial.service.forbiddenWord.TestReviewService;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.SpyBean;
//import org.springframework.context.annotation.Import;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//
//
//import static org.mockito.Mockito.doReturn;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//@Import({ForbiddenWordAspect.class, ForbiddenWordCheckerServiceImpl.class, TestReviewService.class})
//public class ForbiddenWordAspectIntegrationTest {
//
//    @Autowired
//    MockMvc mockMvc;
//
//    @SpyBean
//    ForbiddenWordCheckerServiceImpl checkerService;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Test
//    void 금칙어_포함시_예외_JSON_검증() throws Exception {
//        ReviewRequestDto dto = new ReviewRequestDto();
//        dto.setReviewText("이거 금지어 포함");
//
//        String json = objectMapper.writeValueAsString(dto);
//        doReturn(true).when(checkerService).containsForbiddenWord("이거 금지어 포함");
//
//        mockMvc.perform(post("/reviews")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(json))
//                .andDo(print())
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.success").value(false))
//                .andExpect(jsonPath("$.code").value(400))
//                .andExpect(jsonPath("$.forbiddenPoint").value("letterContents"))
//                .andExpect(jsonPath("$.message").value("금칙어가 포함되어 있습니다."));
//    }
//
//    @Test
//    void 금칙어_없을시_정상처리_JSON_검증() throws Exception {
//        ReviewRequestDto dto = new ReviewRequestDto();
//        dto.setReviewText("이거 금지어 아님");
//
//        String json = objectMapper.writeValueAsString(dto);
//        doReturn(false).when(checkerService).containsForbiddenWord("이거 금지어 포함되지 않음");
//
//        mockMvc.perform(post("/reviews")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(json))
//                .andDo(print())
//                .andExpect(status().is2xxSuccessful())
//                .andExpect(jsonPath("$.status").value("success"))
//                .andExpect(jsonPath("$.message").value("리뷰가 정상적으로 저장되었습니다."));
//    }
//}
