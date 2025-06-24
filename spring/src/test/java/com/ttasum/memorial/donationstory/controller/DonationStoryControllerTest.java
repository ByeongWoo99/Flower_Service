//package com.ttasum.memorial.donationstory.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.ttasum.memorial.controller.donationStory.DonationStoryController;
//import com.ttasum.memorial.dto.donationStory.request.DonationStoryCreateRequestDto;
//import com.ttasum.memorial.dto.donationStory.request.DonationStoryDeleteRequestDto;
//import com.ttasum.memorial.dto.donationStory.request.DonationStoryPasswordVerifyDto;
//import com.ttasum.memorial.dto.donationStory.request.DonationStoryUpdateRequestDto;
//import com.ttasum.memorial.dto.donationStory.response.DonationStoryListResponseDto;
//import com.ttasum.memorial.dto.donationStory.response.DonationStoryPasswordVerifyResponseDto;
//import com.ttasum.memorial.dto.donationStory.response.DonationStoryResponseDto;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.mockito.BDDMockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//import static org.hamcrest.Matchers.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@WebMvcTest(DonationStoryController.class)
//class DonationStoryControllerTest {
//
//    @Autowired
//    private MockMvc mvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @MockBean
//    private com.ttasum.memorial.service.donationStory.DonationStoryService donationStoryService;
//
//    @Test
//    @DisplayName("기증후 스토리 목록 조회 성공")
//    void getStories_Success() throws Exception {
//        // given
//        DonationStoryListResponseDto dto1 = DonationStoryListResponseDto.builder()
//                .storySeq(1)
//                .storyTitle("제목1")
//                .storyWriter("코디네이터 김*연")
//                .donorName("기증자1")
//                .writeTime(LocalDateTime.now())
//                .readCount(100)
//                .commentCount(3)
//                .build();
//
//        DonationStoryListResponseDto dto2 = DonationStoryListResponseDto.builder()
//                .storySeq(2)
//                .storyTitle("제목2")
//                .storyWriter("코디네이터 이*희")
//                .donorName("기증자2")
//                .writeTime(LocalDateTime.now())
//                .readCount(200)
//                .commentCount(5)
//                .build();
//
//        Page<DonationStoryListResponseDto> page =
//                new PageImpl<>(List.of(dto1, dto2), PageRequest.of(0, 2), 2);
//
//        BDDMockito.given(donationStoryService.searchStories(
//                BDDMockito.eq("all"),
//                BDDMockito.isNull(),
//                BDDMockito.eq(PageRequest.of(0, 2))
//        )).willReturn(page);
//
//        // when & then
//        mvc.perform(get("/donationLetters")
//                        .param("page", "0")
//                        .param("size", "2"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.content", hasSize(2)))
//                .andExpect(jsonPath("$.content[0].storySeq", is(1)))
//                .andExpect(jsonPath("$.content[0].storyTitle", is("제목1")))
//                .andExpect(jsonPath("$.content[1].storySeq", is(2)))
//                .andExpect(jsonPath("$.content[1].storyTitle", is("제목2")));
//    }
//
//    @Test
//    @DisplayName("기증후 스토리 단건 조회 성공")
//    void getStory_Success() throws Exception {
//        // given: Builder로 모든 필드 초기화
//        DonationStoryResponseDto dto = DonationStoryResponseDto.builder()
//                .storySeq(42)
//                .storyTitle("단건 제목")
//                .donorName("기증자")
//                .storyContents("내용")
//                .writeTime(LocalDateTime.now())
//                .build();
//
//        // 서비스가 storySeq=42일 때 위 DTO를 반환하도록 스텁
//        BDDMockito.given(donationStoryService.getStory(42))
//                .willReturn(dto);
//
//        // when & then: 반드시 /donationLetters/42 로 호출
//        mvc.perform(get("/donationLetters/42"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.storySeq").value(42))
//                // JSON 프로퍼티 명은 storyTitle 이므로, title이 아니라 storyTitle로 검증
//                .andExpect(jsonPath("$.storyTitle").value("단건 제목"));
//    }
//
//    @Test
//    @DisplayName("스토리 등록 요청 성공 시 200 OK + 메시지 반환")
//    void createStory_Success() throws Exception {
//        // given: 요청 DTO 준비
//        DonationStoryCreateRequestDto req = new DonationStoryCreateRequestDto();
//        req.setAreaCode("A01");
//        req.setStoryTitle("새 제목");
//        req.setDonorName("기증자");
//        req.setStoryPasscode("asdf1234");
//        req.setStoryWriter("테스트");
//        req.setAnonymityFlag("N");
//        req.setStoryContents("새 내용");
//        req.setFileName("test.jpg");
//        req.setOrgFileName("test.jpg");
//        req.setCaptchaToken("token");
//        req.setLetterFont(1);
//        req.setLetterPaper(1);
//
//        // 서비스 동작 스텁 (컨트롤러는 반환값을 응답하지 않습니다)
//        BDDMockito.given(donationStoryService.createStory(
//                BDDMockito.any(DonationStoryCreateRequestDto.class)
//        )).willReturn(DonationStoryResponseDto.builder().storySeq(123).build());
//
//        // when & then
//        mvc.perform(post("/donationLetters")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(req)))
//                .andExpect(status().isOk())
//                // status, data 경로 삭제 — 실제로는 message만 내려옵니다
//                .andExpect(jsonPath("$.message", containsString("등록되었습니다")));
//    }
//
//
//    @Test
//    @DisplayName("스토리 수정 요청 성공 시 200 OK 반환")
//    void updateStory_Success() throws Exception {
//        DonationStoryUpdateRequestDto req = new DonationStoryUpdateRequestDto();
//        req.setStoryPasscode("asdf1234");
//        req.setStoryTitle("수정된 제목");
//        req.setStoryWriter("test");
//        req.setStoryContents("test");
//        req.setLetterFont(1);
//        req.setLetterPaper(1);
//
//
//        BDDMockito.doNothing()
//                .when(donationStoryService).updateStory(BDDMockito.eq(5), BDDMockito.any());
//
//        mvc.perform(patch("/donationLetters/5")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(req)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.message", containsString("수정되었습니다")));
//    }
//
//    @Test
//    @DisplayName("비밀번호 검증 요청 성공 시 200 OK + 결과 DTO 반환")
//    void verifyPwd_Success() throws Exception {
//        // given: 유효한 비밀번호로 DTO 설정 (8자리 이상, 영문자 포함)
//        DonationStoryPasswordVerifyDto req = new DonationStoryPasswordVerifyDto();
//        req.setStoryPasscode("password1");
//
//        // 서비스 stub: 입력이 "password1"일 때 result=1 반환
//        DonationStoryPasswordVerifyResponseDto resp =
//                new DonationStoryPasswordVerifyResponseDto(1, "비밀번호가 일치합니다.");
//        BDDMockito.given(donationStoryService.verifyStoryPasscode(7, "password1"))
//                .willReturn(resp);
//
//        // when & then
//        mvc.perform(post("/donationLetters/7/verifyPwd")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(req)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.result").value(1))
//                .andExpect(jsonPath("$.message").value("비밀번호가 일치합니다."));
//    }
//
//
//    @Test
//    @DisplayName("스토리 삭제 요청 성공 시 200 OK 반환")
//    void deleteStory_Success() throws Exception {
//        // valid passcode(8자 이상, 영문자 포함)
//        DonationStoryDeleteRequestDto req = new DonationStoryDeleteRequestDto();
//        req.setModifierId("mod");
//        req.setStoryPasscode("password1");
//
//        // 서비스 stub: 8번에 대해서는 삭제 성공(true)
//        BDDMockito.given(donationStoryService.softDeleteStory(8, "password1", "mod"))
//                .willReturn(true);
//
//        mvc.perform(delete("/donationLetters/8")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(req)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.message", containsString("삭제되었습니다")));
//    }
//
//    @Test
//    @DisplayName("스토리 삭제 시 패스코드 불일치 시 400 Bad Request 반환")
//    void deleteStory_BadRequest() throws Exception {
//        DonationStoryDeleteRequestDto req = new DonationStoryDeleteRequestDto();
//        req.setModifierId("mod");
//        req.setStoryPasscode("password1");
//
//        // 서비스 stub: 9번에 대해서는 삭제 실패(false)
//        BDDMockito.given(donationStoryService.softDeleteStory(9, "password1", "mod"))
//                .willReturn(false);
//
//        mvc.perform(delete("/donationLetters/9")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(req)))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.message", containsString("비밀번호가 일치하지 않습니다")));
//    }
//
//}
