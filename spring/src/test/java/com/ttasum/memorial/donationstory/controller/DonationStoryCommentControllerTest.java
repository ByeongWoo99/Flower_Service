package com.ttasum.memorial.donationstory.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ttasum.memorial.controller.donationStory.DonationStoryCommentController;
import com.ttasum.memorial.dto.donationStoryComment.request.*;
import com.ttasum.memorial.service.donationStory.DonationStoryCommentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DonationStoryCommentController.class)
class DonationStoryCommentControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DonationStoryCommentService commentService;

    @Test
    @DisplayName("댓글 등록 요청 성공 시 200 OK + 메시지 반환")
    void createComment_Success() throws Exception {
        DonationStoryCommentCreateRequestDto req = new DonationStoryCommentCreateRequestDto();
        req.setCommentWriter("홍길동");
        req.setCommentPasscode("password1");    // 8자 이상, 영문자 포함
        req.setContents("댓글입니다.");

        // void 메서드 stub: createComment(3, any) → doNothing
        doNothing()
                .when(commentService)
                .createComment(eq(3), any(DonationStoryCommentCreateRequestDto.class));

        mvc.perform(post("/donationLetters/3/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", containsString("등록되었습니다")));
    }

    @Test
    @DisplayName("댓글 수정 요청 성공 시 200 OK 반환")
    void updateComment_Success() throws Exception {
        // 1) 유효한 패스코드 + 내용 세팅
        DonationStoryCommentUpdateRequestDto req = new DonationStoryCommentUpdateRequestDto();
        req.setCommentPasscode("password1");    // 8자 이상, 영문자 포함
        req.setContents("수정된 댓글");

        // 2) 서비스는 void 메서드이므로 doNothing()으로 stub
        doNothing()
                .when(commentService)
                .updateComment(3, 5, req);

        // 3) PATCH 요청, JSON 바디엔 commentPasscode, contents 만 포함
        mvc.perform(patch("/donationLetters/3/comments/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", containsString("수정되었습니다")));
    }


    @Test
    @DisplayName("댓글 삭제 요청 성공 시 200 OK 반환")
    void deleteComment_Success() throws Exception {
        // 1) 유효한 패스코드만 세팅 (8~16자, 영문자 포함)
        DonationStoryCommentDeleteRequestDto req = new DonationStoryCommentDeleteRequestDto();
        req.setCommentPasscode("password1");

        // 2) 서비스는 void 이므로 doNothing() 으로 stub
        doNothing()
                .when(commentService)
                .softDeleteComment(3, 7, req);

        // 3) DELETE 요청, JSON 바디에는 오직 commentPasscode만
        mvc.perform(delete("/donationLetters/3/comments/7")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", containsString("삭제되었습니다")));
    }

}
