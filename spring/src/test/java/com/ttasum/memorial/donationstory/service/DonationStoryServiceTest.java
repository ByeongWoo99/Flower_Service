//package com.ttasum.memorial.donationstory.service;
//
//import com.ttasum.memorial.domain.entity.donationStory.DonationStory;
//import com.ttasum.memorial.domain.entity.donationStory.DonationStoryComment;
//import com.ttasum.memorial.domain.repository.donationStory.DonationStoryCommentRepository;
//import com.ttasum.memorial.domain.repository.donationStory.DonationStoryRepository;
//import com.ttasum.memorial.dto.donationStory.request.DonationStoryCreateRequestDto;
//import com.ttasum.memorial.dto.donationStory.request.DonationStoryUpdateRequestDto;
//import com.ttasum.memorial.dto.donationStory.response.DonationStoryListResponseDto;
//import com.ttasum.memorial.dto.donationStory.response.DonationStoryPasswordVerifyResponseDto;
//import com.ttasum.memorial.dto.donationStory.response.DonationStoryResponseDto;
//import com.ttasum.memorial.exception.common.badRequest.*;
//import com.ttasum.memorial.exception.donationStory.DonationStoryNotFoundException;
//import com.ttasum.memorial.service.common.CaptchaVerifier;
//import com.ttasum.memorial.service.donationStory.DonationStoryService;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.*;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.data.domain.*;
//import org.springframework.test.util.ReflectionTestUtils;
//
//import java.util.*;
//
//import static org.assertj.core.api.Assertions.*;
//import static org.junit.jupiter.api.Assertions.assertAll;
//import static org.mockito.BDDMockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class DonationStoryServiceTest {
//
//    @Mock private DonationStoryRepository donationStoryRepository;
//    @Mock private DonationStoryCommentRepository commentRepository;
//    @Mock private CaptchaVerifier captchaVerifier;
//
//    @InjectMocks private DonationStoryService donationStoryService;
//
//    @Captor private ArgumentCaptor<DonationStory> storyCaptor;
//
//    @Test
//    @DisplayName("음수 페이지 번호로 검색 시 예외 발생")
//    void searchStories_InvalidPageNumber() {
//        Pageable badPage = mock(Pageable.class);
//        given(badPage.getPageNumber()).willReturn(-1);
//
//        assertThatThrownBy(() ->
//                donationStoryService.searchStories("all", null, badPage)
//        ).isInstanceOf(InvalidPaginationParameterException.class);
//    }
//
//
//    @Test
//    @DisplayName("잘못된 검색 필드로 검색 시 예외 발생")
//    void searchStories_InvalidField() {
//        Pageable page = PageRequest.of(0, 10);
//        assertThatThrownBy(() -> donationStoryService.searchStories("invalid", null, page))
//                .isInstanceOf(InvalidSearchFieldException.class);
//    }
//
//    @Test
//    @DisplayName("너무 짧은 검색어로 검색 시 예외 발생")
//    void searchStories_InvalidKeywordLength() {
//        Pageable page = PageRequest.of(0, 10);
//        String kw = "a";  //길이 1 < MIN(2)
//        assertThatThrownBy(() -> donationStoryService.searchStories("all", kw, page))
//                .isInstanceOf(InvalidKeywordLengthException.class);
//    }
//
//    @Test
//    @DisplayName("올바른 입력값으로 검색 시 DTO 페이지 반환")
//    void searchStories_Success() {
//        Pageable page = PageRequest.of(0, 1);
//        DonationStory entity = DonationStory.builder()
//                .areaCode("A")
//                .title("Title")
//                .donorName("Donor")
//                .passcode("pw")
//                .writer("User")
//                .anonymityFlag("N")
//                .readCount(0)
//                .contents("C")
//                .fileName(null)
//                .originalFileName(null)
//                .writerId(null)
//                .modifierId(null)
//                .build();
//        ReflectionTestUtils.setField(entity, "id", 42);
//
//        Page<DonationStory> mockPage = new PageImpl<>(List.of(entity), page, 1);
//        given(donationStoryRepository.searchStories("title", "test", page))
//                .willReturn(mockPage);
//
//        Page<DonationStoryListResponseDto> result =
//                donationStoryService.searchStories("title", "test", page);
//
//        assertThat(result.getTotalElements()).isEqualTo(1);
//        assertThat(result.getContent().get(0).getStorySeq()).isEqualTo(42);
//    }
//
//    @Test
//    @DisplayName("유효한 캡차일 경우 저장하고 DTO 반환")
//    void createStory_Success() {
//        // DTO 스파이로 toEntity() 결과 제어
//        DonationStoryCreateRequestDto dto = spy(new DonationStoryCreateRequestDto());
//        dto.setCaptchaToken("valid-token");
//        // toEntity()가 반환할 엔티티 준비
//        DonationStory toSave = DonationStory.builder()
//                .areaCode("A")
//                .title("T")
//                .donorName("D")
//                .passcode("pw")
//                .writer("U")
//                .anonymityFlag("N")
//                .contents("body")
//                .build();
//        ReflectionTestUtils.setField(toSave, "id", 100);
//        doReturn(toSave).when(dto).toEntity();
//
//        given(captchaVerifier.verifyCaptcha("valid-token")).willReturn(true);
//        given(donationStoryRepository.save(toSave)).willReturn(toSave);
//
//        DonationStoryResponseDto res = donationStoryService.createStory(dto);
//
//        then(captchaVerifier).should().verifyCaptcha("valid-token");
//        then(donationStoryRepository).should().save(storyCaptor.capture());
//
//        DonationStory saved = storyCaptor.getValue();
//        assertThat(res.getStorySeq()).isEqualTo(100);
//        assertThat(saved.getTitle()).isEqualTo("T");
//    }
//
//    @Test
//    @DisplayName("잘못된 캡차일 경우 예외 발생")
//    void createStory_InvalidCaptcha() {
//        DonationStoryCreateRequestDto dto = new DonationStoryCreateRequestDto();
//        dto.setCaptchaToken("bad");
//        given(captchaVerifier.verifyCaptcha("bad")).willReturn(false);
//
//        assertThatThrownBy(() -> donationStoryService.createStory(dto))
//                .isInstanceOf(CaptchaVerificationFailedException.class);
//        then(donationStoryRepository).should(never()).save(any());
//    }
//
//    @Test
//    @DisplayName("존재하는 스토리 조회 시 조회수 증가 및 댓글 포함")
//    void getStory_Success() {
//        DonationStory story = DonationStory.builder()
//                .areaCode("A")
//                .title("T")
//                .donorName("D")
//                .passcode("pw")
//                .writer("U")
//                .anonymityFlag("N")
//                .readCount(5)
//                .contents("C")
//                .build();
//        ReflectionTestUtils.setField(story, "id", 1);
//
//        DonationStoryComment comment = DonationStoryComment.builder()
//                .story(story)
//                .writer("X")
//                .passcode("pw")
//                .contents("c")
//                .build();
//
//        given(donationStoryRepository.findByIdAndDelFlag(1, "N"))
//                .willReturn(Optional.of(story));
//        given(commentRepository.findByStory_IdAndDelFlagOrderByWriteTimeAsc(1, "N"))
//                .willReturn(List.of(comment));
//
//        DonationStoryResponseDto dto = donationStoryService.getStory(1);
//
//        // readCount가 5→6으로 증가
//        assertThat(dto.getReadCount()).isEqualTo(6);
//        assertThat(dto.getComments()).hasSize(1);
//    }
//
//    @Test
//    @DisplayName("존재하지 않는 스토리 조회 시 예외 발생")
//    void getStory_NotFound() {
//        given(donationStoryRepository.findByIdAndDelFlag(2, "N"))
//                .willReturn(Optional.empty());
//
//        assertThatThrownBy(() -> donationStoryService.getStory(2))
//                .isInstanceOf(DonationStoryNotFoundException.class);
//    }
//
//    @Test
//    @DisplayName("존재하는 스토리 수정 시 엔티티의 update 메서드 호출")
//    void updateStory_Success() {
//        DonationStoryUpdateRequestDto dto = new DonationStoryUpdateRequestDto();
//        dto.setStoryTitle("New Title");
//
//        DonationStory entity = spy(DonationStory.builder()
//                .areaCode("A")
//                .title("Old Title")
//                .donorName("D")
//                .passcode("pw")
//                .writer("U")
//                .anonymityFlag("N")
//                .contents("C")
//                .build());
//        ReflectionTestUtils.setField(entity, "id", 5);
//
//        given(donationStoryRepository.findByIdAndDelFlag(5, "N"))
//                .willReturn(Optional.of(entity));
//
//        donationStoryService.updateStory(5, dto);
//
//        then(entity).should().update(dto);
//    }
//
//    @Test
//    @DisplayName("존재하지 않는 스토리 수정 시 예외 발생")
//    void updateStory_NotFound() {
//        given(donationStoryRepository.findByIdAndDelFlag(9, "N"))
//                .willReturn(Optional.empty());
//
//        assertThatThrownBy(() -> donationStoryService.updateStory(9, new DonationStoryUpdateRequestDto()))
//                .isInstanceOf(DonationStoryNotFoundException.class);
//    }
//
//    @Test
//    @DisplayName("비밀번호가 일치하면 성공 응답 반환")
//    void verifyStoryPasscode_Match() {
//        DonationStory story = DonationStory.builder()
//                .areaCode("A")
//                .title("T")
//                .donorName("D")
//                .passcode("pw")
//                .writer("U")
//                .anonymityFlag("N")
//                .contents("C")
//                .build();
//        given(donationStoryRepository.findByIdAndDelFlag(3, "N"))
//                .willReturn(Optional.of(story));
//
//        DonationStoryPasswordVerifyResponseDto res =
//                donationStoryService.verifyStoryPasscode(3, "pw");
//
//        assertThat(res.getResult()).isEqualTo(1);
//        assertThat(res.getMessage()).isEqualTo("비밀번호가 일치합니다.");
//    }
//
//    @Test
//    @DisplayName("비밀번호가 틀리거나 스토리가 없으면 실패 응답 반환")
//    void verifyStoryPasscode_NoMatch() {
//        // 잘못된 비밀번호
//        DonationStory story = DonationStory.builder()
//                .areaCode("A")
//                .title("T")
//                .donorName("D")
//                .passcode("pw")
//                .writer("U")
//                .anonymityFlag("N")
//                .contents("C")
//                .build();
//        given(donationStoryRepository.findByIdAndDelFlag(4, "N"))
//                .willReturn(Optional.of(story));
//        DonationStoryPasswordVerifyResponseDto r1 =
//                donationStoryService.verifyStoryPasscode(4, "xx");
//
//        // 스토리 미존재
//        given(donationStoryRepository.findByIdAndDelFlag(5, "N"))
//                .willReturn(Optional.empty());
//        DonationStoryPasswordVerifyResponseDto r2 =
//                donationStoryService.verifyStoryPasscode(5, "pw");
//
//        assertAll(
//                () -> assertThat(r1.getResult()).isEqualTo(0),
//                () -> assertThat(r1.getMessage()).isEqualTo("비밀번호가 일치하지 않습니다."),
//                () -> assertThat(r2.getResult()).isEqualTo(0),
//                () -> assertThat(r2.getMessage()).isEqualTo("비밀번호가 일치하지 않습니다.")
//        );
//    }
//
//    @Test
//    @DisplayName("비밀번호 일치 시 스토리 삭제 처리 및 true 반환")
//    void softDeleteStory_Success() {
//        DonationStory story = DonationStory.builder()
//                .areaCode("A")
//                .title("T")
//                .donorName("D")
//                .passcode("pw")
//                .writer("U")
//                .anonymityFlag("N")
//                .contents("C")
//                .build();
//        ReflectionTestUtils.setField(story, "id", 7);
//        given(donationStoryRepository.findByIdAndDelFlag(7, "N"))
//                .willReturn(Optional.of(story));
//
//        boolean result = donationStoryService.softDeleteStory(7, "pw", "mod");
//
//        assertThat(result).isTrue();
//        assertThat(story.getDelFlag()).isEqualTo("Y");
//    }
//
//    @Test
//    @DisplayName("비밀번호가 틀릴 경우 삭제되지 않고 false 반환")
//    void softDeleteStory_WrongPasscode() {
//        DonationStory story = DonationStory.builder()
//                .areaCode("A")
//                .title("T")
//                .donorName("D")
//                .passcode("pw")
//                .writer("U")
//                .anonymityFlag("N")
//                .contents("C")
//                .build();
//        given(donationStoryRepository.findByIdAndDelFlag(8, "N"))
//                .willReturn(Optional.of(story));
//
//        boolean result = donationStoryService.softDeleteStory(8, "xx", "mod");
//
//        assertThat(result).isFalse();
//        assertThat(story.getDelFlag()).isEqualTo("N");
//    }
//
//    @Test
//    @DisplayName("스토리가 존재하지 않으면 예외 발생")
//    void softDeleteStory_NotFound() {
//        given(donationStoryRepository.findByIdAndDelFlag(9, "N"))
//                .willReturn(Optional.empty());
//
//        assertThatThrownBy(() -> donationStoryService.softDeleteStory(9, "pw", "mod"))
//                .isInstanceOf(DonationStoryNotFoundException.class);
//    }
//}
