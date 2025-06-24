//package com.ttasum.memorial.rememberance.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.ttasum.memorial.controller.memorial.MemorialController;
//import com.ttasum.memorial.dto.memorial.response.MemorialDetailResponseDto;
//import com.ttasum.memorial.dto.memorial.response.MemorialResponseDto;
//import com.ttasum.memorial.dto.memorialComment.request.MemorialReplyCreateRequestDto;
//import com.ttasum.memorial.dto.memorialComment.request.MemorialReplyDeleteRequestDto;
//import com.ttasum.memorial.dto.memorialComment.request.MemorialReplyUpdateRequestDto;
//import com.ttasum.memorial.dto.memorialComment.response.MemorialReplyResponseDto;
//import com.ttasum.memorial.service.memorial.MemorialService;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.List;
//
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.BDDMockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@WebMvcTest(MemorialController.class)
//class MemorialControllerTest {
//
//    @Autowired
//    private MockMvc mvc;
//
//    @Autowired
//    private ObjectMapper om;
//
//    @MockBean
//    private MemorialService memorialService;
//
//    @Test
//    @DisplayName("GET /remembrance - 기본 목록 조회")
//    void getStories_DefaultPaging_Success() throws Exception {
//        // given
//        var dto1 = new MemorialResponseDto(
//                1, "홍길동", "Y","M", 45, "20230101", 2L,
//                LocalDate.now().minusDays(2)
//        );
//        var dto2 = new MemorialResponseDto(
//                2, "김철수", "N","F", 50, "20230202", 0L,
//                LocalDate.now().minusDays(2)
//        );
//        Page<MemorialResponseDto> page = new PageImpl<>(
//                List.of(dto1, dto2),
//                PageRequest.of(0, 20),
//                2
//        );
//        given(memorialService.getMemorialList(
//                isNull(), isNull(), isNull(),
//                any(Pageable.class), eq("donateDate"), eq("Desc")
//        )).willReturn(page);
//
//        // when / then
//        mvc.perform(get("/remembrance"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.content").isArray())
//                .andExpect(jsonPath("$.content[0].donorName").value("홍*동"))
//                .andExpect(jsonPath("$.totalElements").value(2));
//    }
//
//    @Test
//    @DisplayName("GET /remembrance/{id} - 상세 조회")
//    void getMemorialDetail_Success() throws Exception {
//        // given
//        var detailDto = MemorialDetailResponseDto.builder()
//                .donateSeq(1)
//                .donorName("홍길동")
//                .donateDate("20240101")         // yyyyMMdd 형식
//                .contents("추모 내용")
//                .genderFlag("N")
//                .donateAge(50)
//                .flowerCount(0)
//                .loveCount(0)
//                .seeCount(0)
//                .missCount(0)
//                .proudCount(0)
//                .hardCount(0)
//                .sadCount(0)
//                .isNew(true)
//                .replies(List.of())             // 댓글 DTO 리스트
//                .heavenLetters(List.of())       // 천국편지 DTO 리스트
//                .build();
//        given(memorialService.getMemorialDetail(1)).willReturn(detailDto);
//
//        // when / then
//        mvc.perform(get("/remembrance/1"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.donorName").value("홍길동"))
//                .andExpect(jsonPath("$.contents").value("추모 내용"));
//    }
//
//    @Test
//    @DisplayName("POST /remembrance/{id}/emoji - 이모지 증가")
//    void incrementEmoji_Success() throws Exception {
//        // service 호출 예외 없음
//        willDoNothing().given(memorialService).incrementEmoji(1, "flower");
//
//        mvc.perform(post("/remembrance/1/emoji")
//                        .param("emoji", "flower"))
//                .andExpect(status().isOk())
//                .andExpect(content().string("")); // Void body
//    }
//
//    @Test
//    @DisplayName("POST /remembrance/{id}/comments - 댓글 등록")
//    void createReply_Success() throws Exception {
//        // given
//        var req = new MemorialReplyCreateRequestDto();
//        req.setCaptchaToken("token");
//        req.setReplyWriter("writer");
//        req.setReplyPassword("pass1234");
//        req.setReplyContents("댓글");
//
//        // 서비스가 반환할 DTO - 모든 파라미터를 생성자에 전달
//        LocalDateTime now = LocalDateTime.now();
//        var respDto = new MemorialReplyResponseDto(
//                10, 1, "writer", null,
//                "댓글", now, null, null
//        );
//        given(memorialService.createReply(eq(1), any()))
//                .willReturn(respDto);
//
//        // when / then
//        mvc.perform(post("/remembrance/1/comments")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(om.writeValueAsString(req)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value(201))
//                .andExpect(jsonPath("$.message").value("댓글이 성공적으로 등록되었습니다."));
//
//    }
//
//    @Test
//    @DisplayName("PATCH /remembrance/{id}/comments/{replyId} - 댓글 수정")
//    void updateReply_Success() throws Exception {
//        var req = new MemorialReplyUpdateRequestDto();
//        req.setReplyPassword("pass1234");
//        req.setReplyContents("수정댓글");
//        LocalDateTime now = LocalDateTime.now();
//        var respDto = new MemorialReplyResponseDto(
//                10, 1, "writer", null,
//                "댓글", now, null, null
//        );        given(memorialService.updateReply(eq(1), eq(10), any()))
//                .willReturn(respDto);
//
//        mvc.perform(patch("/remembrance/1/comments/10")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(om.writeValueAsString(req)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value(200))
//                .andExpect(jsonPath("$.message").value("댓글이 성공적으로 수정되었습니다."));
//    }
//
//    @Test
//    @DisplayName("DELETE /remembrance/{id}/comments/{replyId} - 댓글 삭제")
//    void deleteReply_Success() throws Exception {
//        var req = new MemorialReplyDeleteRequestDto();
//        req.setReplyPassword("pass1234");
//        willDoNothing().given(memorialService)
//                .softDeleteReply(1, 10, req);
//
//        mvc.perform(delete("/remembrance/1/comments/10")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(om.writeValueAsString(req)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value(200))
//                .andExpect(jsonPath("$.message").value("댓글이 성공적으로 삭제되었습니다."));
//    }
//}
