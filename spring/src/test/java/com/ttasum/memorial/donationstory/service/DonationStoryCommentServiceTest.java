//package com.ttasum.memorial.donationstory.service;
//
//import com.ttasum.memorial.domain.entity.donationStory.DonationStory;
//import com.ttasum.memorial.domain.entity.donationStory.DonationStoryComment;
//import com.ttasum.memorial.domain.repository.donationStory.DonationStoryCommentRepository;
//import com.ttasum.memorial.domain.repository.donationStory.DonationStoryRepository;
//import com.ttasum.memorial.dto.donationStoryComment.request.DonationStoryCommentCreateRequestDto;
//import com.ttasum.memorial.dto.donationStoryComment.request.DonationStoryCommentDeleteRequestDto;
//import com.ttasum.memorial.dto.donationStoryComment.request.DonationStoryCommentUpdateRequestDto;
//import com.ttasum.memorial.dto.donationStoryComment.response.DonationStoryCommentResponseDto;
//import com.ttasum.memorial.exception.common.badRequest.CaptchaVerificationFailedException;
//import com.ttasum.memorial.exception.common.badRequest.InvalidCommentPasscodeException;
//import com.ttasum.memorial.exception.donationStory.DonationStoryCommentNotFoundException;
//import com.ttasum.memorial.exception.donationStory.DonationStoryNotFoundException;
//import com.ttasum.memorial.service.common.CaptchaVerifier;
//import com.ttasum.memorial.service.donationStory.DonationStoryCommentService;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.*;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.test.util.ReflectionTestUtils;
//
//import java.time.LocalDateTime;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.*;
//import static org.junit.jupiter.api.Assertions.assertAll;
//import static org.mockito.BDDMockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class DonationStoryCommentServiceTest {
//
//    @Mock
//    private DonationStoryRepository storyRepository;
//
//    @Mock
//    private DonationStoryCommentRepository commentRepository;
//
//    @Mock
//    private CaptchaVerifier captchaVerifier;
//
//    @InjectMocks
//    private DonationStoryCommentService commentService;
//
//    @Captor
//    private ArgumentCaptor<DonationStoryComment> commentCaptor;
//
//    @Test
//    void createComment_Success() {
//        // given
//        Integer storySeq = 1;
//        DonationStoryCommentCreateRequestDto dto = new DonationStoryCommentCreateRequestDto();
//        dto.setCommentWriter("홍길동");
//        dto.setCommentPasscode("pass1234");
//        dto.setContents("좋은 이야기입니다.");
//        dto.setCaptchaToken("valid-token");
//
//        DonationStory story = DonationStory.builder()
//                .areaCode("A01")
//                .title("제목")
//                .donorName("기증자")
//                .passcode("spw")
//                .writer("작성자")
//                .anonymityFlag("N")
//                .build();
//
//        given(captchaVerifier.verifyCaptcha("valid-token")).willReturn(true);
//        given(storyRepository.findByIdAndDelFlag(storySeq, "N"))
//                .willReturn(Optional.of(story));
//        // commentRepository.save()는 실제 데이터를 반환하지만, void 반환이라 동작 검증만 합니다.
//        given(commentRepository.save(any(DonationStoryComment.class)))
//                .willReturn(DonationStoryComment.builder().build());
//
//        // when
//        commentService.createComment(storySeq, dto);
//
//        // then
//        then(captchaVerifier).should().verifyCaptcha("valid-token");
//        then(storyRepository).should().findByIdAndDelFlag(storySeq, "N");
//        then(commentRepository).should().save(commentCaptor.capture());
//
//        DonationStoryComment captured = commentCaptor.getValue();
//        assertAll("저장된 댓글 검증",
//                () -> assertThat(captured.getStory()).isSameAs(story),
//                () -> assertThat(captured.getWriter()).isEqualTo("홍길동"),
//                () -> assertThat(captured.getPasscode()).isEqualTo("pass1234"),
//                () -> assertThat(captured.getContents()).isEqualTo("좋은 이야기입니다.")
//        );
//    }
//
//    @Test
//    void createComment_InvalidCaptcha() {
//        // given
//        DonationStoryCommentCreateRequestDto dto = new DonationStoryCommentCreateRequestDto();
//        dto.setCaptchaToken("bad-token");
//        given(captchaVerifier.verifyCaptcha("bad-token")).willReturn(false);
//
//        // when & then
//        assertThatThrownBy(() -> commentService.createComment(1, dto))
//                .isInstanceOf(CaptchaVerificationFailedException.class);
//        then(storyRepository).should(never()).findByIdAndDelFlag(any(), any());
//        then(commentRepository).should(never()).save(any());
//    }
//
//    @Test
//    void createComment_StoryNotFound() {
//        // given
//        DonationStoryCommentCreateRequestDto dto = new DonationStoryCommentCreateRequestDto();
//        dto.setCaptchaToken("token");
//        given(captchaVerifier.verifyCaptcha("token")).willReturn(true);
//        given(storyRepository.findByIdAndDelFlag(999, "N"))
//                .willReturn(Optional.empty());
//
//        // when & then
//        assertThatThrownBy(() -> commentService.createComment(999, dto))
//                .isInstanceOf(DonationStoryNotFoundException.class);
//        then(commentRepository).should(never()).save(any());
//    }
//
//
//    @Test
//    @DisplayName("given non-deleted comments when getComments then return mapped DTO list sorted by writeTime")
//    void getComments_ShouldReturnDtoList() {
//        // given
//        Integer storySeq = 1;
//        DonationStoryComment c1 = DonationStoryComment.builder()
//                .writer("Alice").contents("첫 댓글").build();
//        DonationStoryComment c2 = DonationStoryComment.builder()
//                .writer("Bob").contents("두번째 댓글").build();
//        // 엔티티에 PK·작성시간 설정 (DTO 매핑 검증용)
//        ReflectionTestUtils.setField(c1, "commentSeq", 1);
//        ReflectionTestUtils.setField(c1, "writeTime", LocalDateTime.of(2025, 6, 10, 9, 0));
//        ReflectionTestUtils.setField(c2, "commentSeq", 2);
//        ReflectionTestUtils.setField(c2, "writeTime", LocalDateTime.of(2025, 6, 10, 10, 0));
//
//        given(commentRepository.findByStory_IdAndDelFlagOrderByWriteTimeAsc(storySeq, "N"))
//                .willReturn(Arrays.asList(c1, c2));
//
//        // when
//        List<DonationStoryCommentResponseDto> dtos = commentService.getComments(storySeq);
//
//        // then
//        assertAll("DTO 매핑 검증",
//                () -> assertThat(dtos).hasSize(2),
//                () -> assertThat(dtos.get(0).getId()).isEqualTo(1),
//                () -> assertThat(dtos.get(0).getWriter()).isEqualTo("Alice"),
//                () -> assertThat(dtos.get(1).getId()).isEqualTo(2),
//                () -> assertThat(dtos.get(1).getContents()).isEqualTo("두번째 댓글")
//        );
//    }
//
//    @Test
//    @DisplayName("given existing comment and correct passcode when updateComment then delegate to entity")
//    void updateComment_Success() {
//        // given
//        Integer storySeq = 1, commentSeq = 10;
//        DonationStoryCommentUpdateRequestDto dto = new DonationStoryCommentUpdateRequestDto();
//        dto.setCommentPasscode("secret");
//        dto.setContents("수정된 내용");
//
//        DonationStoryComment existing = spy(DonationStoryComment.builder()
//                .writer("Writer").passcode("secret").contents("old").build());
//        ReflectionTestUtils.setField(existing, "commentSeq", commentSeq);
//
//        given(commentRepository.findByStory_IdAndCommentSeqAndDelFlag(storySeq, commentSeq, "N"))
//                .willReturn(Optional.of(existing));
//
//        // when
//        commentService.updateComment(storySeq, commentSeq, dto);
//
//        // then
//        then(existing).should().updateComment("수정된 내용", null);
//    }
//
//    @Test
//    void updateComment_NotFound() {
//        // given
//        given(commentRepository.findByStory_IdAndCommentSeqAndDelFlag(1, 99, "N"))
//                .willReturn(Optional.empty());
//        DonationStoryCommentUpdateRequestDto dto = new DonationStoryCommentUpdateRequestDto();
//        dto.setCommentPasscode("any");
//
//        // when & then
//        assertThatThrownBy(() -> commentService.updateComment(1, 99, dto))
//                .isInstanceOf(DonationStoryCommentNotFoundException.class);
//    }
//
//    @Test
//    void updateComment_InvalidPasscode() {
//        // given
//        Integer storySeq = 1, commentSeq = 10;
//        DonationStoryCommentUpdateRequestDto dto = new DonationStoryCommentUpdateRequestDto();
//        dto.setCommentPasscode("wrong");
//
//        DonationStoryComment existing = DonationStoryComment.builder()
//                .writer("Writer").passcode("correct").contents("old").build();
//        ReflectionTestUtils.setField(existing, "commentSeq", commentSeq);
//
//        given(commentRepository.findByStory_IdAndCommentSeqAndDelFlag(storySeq, commentSeq, "N"))
//                .willReturn(Optional.of(existing));
//
//        // when & then
//        assertThatThrownBy(() -> commentService.updateComment(storySeq, commentSeq, dto))
//                .isInstanceOf(InvalidCommentPasscodeException.class);
//    }
//
//    @Test
//    void softDeleteComment_Success() {
//        // given
//        Integer storySeq = 1, commentSeq = 20;
//        DonationStoryCommentDeleteRequestDto dto = new DonationStoryCommentDeleteRequestDto();
//        dto.setCommentPasscode("ok");
//
//        DonationStoryComment existing = spy(DonationStoryComment.builder()
//                .writer("X").passcode("ok").contents("c").build());
//        ReflectionTestUtils.setField(existing, "commentSeq", commentSeq);
//
//        given(commentRepository.findByStory_IdAndCommentSeqAndDelFlag(storySeq, commentSeq, "N"))
//                .willReturn(Optional.of(existing));
//
//        // when
//        commentService.softDeleteComment(storySeq, commentSeq, dto);
//
//        // then
//        then(existing).should().deleteComment(null);
//    }
//
//    @Test
//    void softDeleteComment_NotFound() {
//        // given
//        given(commentRepository.findByStory_IdAndCommentSeqAndDelFlag(1, 99, "N"))
//                .willReturn(Optional.empty());
//        DonationStoryCommentDeleteRequestDto dto = new DonationStoryCommentDeleteRequestDto();
//        dto.setCommentPasscode("any");
//
//        // when & then
//        assertThatThrownBy(() -> commentService.softDeleteComment(1, 99, dto))
//                .isInstanceOf(DonationStoryCommentNotFoundException.class);
//    }
//
//    @Test
//    void softDeleteComment_InvalidPasscode() {
//        // given
//        Integer storySeq = 1, commentSeq = 20;
//        DonationStoryCommentDeleteRequestDto dto = new DonationStoryCommentDeleteRequestDto();
//        dto.setCommentPasscode("bad");
//
//        DonationStoryComment existing = DonationStoryComment.builder()
//                .writer("X").passcode("good").contents("c").build();
//        ReflectionTestUtils.setField(existing, "commentSeq", commentSeq);
//
//        given(commentRepository.findByStory_IdAndCommentSeqAndDelFlag(storySeq, commentSeq, "N"))
//                .willReturn(Optional.of(existing));
//
//        // when & then
//        assertThatThrownBy(() -> commentService.softDeleteComment(storySeq, commentSeq, dto))
//                .isInstanceOf(InvalidCommentPasscodeException.class);
//    }
//}