//package com.ttasum.memorial.rememberance.service;
//
//import com.ttasum.memorial.domain.entity.memorial.Memorial;
//import com.ttasum.memorial.domain.entity.memorial.MemorialReply;
//import com.ttasum.memorial.domain.repository.heavenLetter.HeavenLetterRepository;
//import com.ttasum.memorial.domain.repository.memorial.MemorialReplyRepository;
//import com.ttasum.memorial.domain.repository.memorial.MemorialRepository;
//import com.ttasum.memorial.dto.memorial.response.MemorialDetailResponseDto;
//import com.ttasum.memorial.dto.memorial.response.MemorialResponseDto;
//import com.ttasum.memorial.dto.memorialComment.request.MemorialReplyCreateRequestDto;
//import com.ttasum.memorial.dto.memorialComment.request.MemorialReplyDeleteRequestDto;
//import com.ttasum.memorial.dto.memorialComment.request.MemorialReplyUpdateRequestDto;
//import com.ttasum.memorial.dto.memorialComment.response.MemorialReplyResponseDto;
//import com.ttasum.memorial.exception.common.badRequest.CaptchaVerificationFailedException;
//import com.ttasum.memorial.exception.common.badRequest.InvalidCommentPasscodeException;
//import com.ttasum.memorial.exception.common.badRequest.InvalidPaginationParameterException;
//import com.ttasum.memorial.exception.common.badRequest.InvalidSearchFieldException;
//import com.ttasum.memorial.exception.memorial.MemorialNotFoundException;
//import com.ttasum.memorial.exception.memorial.MemorialReplyNotFoundException;
//import com.ttasum.memorial.service.common.CaptchaVerifier;
//import com.ttasum.memorial.service.memorial.MemorialService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.data.domain.*;
//import org.springframework.test.util.ReflectionTestUtils;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.List;
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class MemorialServiceTest {
//
//    @Mock
//    private MemorialRepository memorialRepository;
//
//    @Mock
//    private MemorialReplyRepository replyRepository;
//
//    @Mock
//    private HeavenLetterRepository heavenLetterRepository;
//
//    @Mock
//    private CaptchaVerifier captchaVerifier;
//
//    @InjectMocks
//    private MemorialService memorialService;
//
//    private final String DONOR_NAME = "홍";
//    private final String START_DATE = "20230101";
//    private final String END_DATE   = "20231231";
//    private final String SORT_FIELD = "donorName";
//    private final String DIRECTION  = "Asc";
//    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyyMMdd");
//    private final Integer DONATE_SEQ = 1;
//    private final Integer REPLY_SEQ  = 10;
//
//    private Memorial dummyMemorial;
//
//    @BeforeEach
//    void setUp() {
//        dummyMemorial = Memorial.builder()
//                .donateSeq(DONATE_SEQ)
//                .donorName("테스트기증자")
//                .anonymityFlag("N")
//                .donateTitle("제목")
//                .areaCode("001")
//                .contents("내용")
//                .fileName(null)
//                .orgFileName(null)
//                .writer("작성자")
//                .donateDate("20240101")
//                .genderFlag("M")
//                .donateAge(0)
//                .writerId("tester")
//                .donorBirthdate(LocalDate.parse("19700101", DateTimeFormatter.ofPattern("yyyyMMdd")))
//                .build();
//    }
//
//    @Test
//    @DisplayName("getMemorialList: 정상 파라미터면 DTO Page 반환")
//    void getMemorialList_ValidParameters_ReturnsDtoPage() {
//        // given
//        Pageable pageable = PageRequest.of(0, 2,
//                Sort.Direction.fromString(DIRECTION), SORT_FIELD);
//
//        List<MemorialResponseDto> dtoList = List.of(
//                new MemorialResponseDto(
//                        1,
//                        "홍길동",
//                        "N",
//                        "M",
//                        45,
//                        "20230601",   // String 타입
//                        3L,           // long 타입 (L 붙이기)
//                        LocalDate.parse("19780321", DateTimeFormatter.ofPattern("yyyyMMdd"))
//                ),
//                new MemorialResponseDto(
//                        2,
//                        "홍순자",
//                        "N",
//                        "F",
//                        52,
//                        "20230315",
//                        0L,
//                        LocalDate.parse("19710715", DateTimeFormatter.ofPattern("yyyyMMdd"))
//                )
//        );
//        Page<MemorialResponseDto> fakePage =
//                new PageImpl<>(dtoList, pageable, dtoList.size());
//
//        when(memorialRepository.findByFilter(
//                DONOR_NAME, START_DATE, END_DATE, pageable))
//                .thenReturn(fakePage);
//
//        // when
//        Page<MemorialResponseDto> result =
//                memorialService.getMemorialList(
//                        DONOR_NAME, START_DATE, END_DATE,
//                        pageable, SORT_FIELD, DIRECTION
//                );
//
//        // then
//        assertThat(result.getTotalElements()).isEqualTo(2);
//        assertThat(result.getContent())
//                .extracting("donorName")
//                .containsExactly("홍길동", "홍순자");
//        verify(memorialRepository, times(1))
//                .findByFilter(DONOR_NAME, START_DATE, END_DATE, pageable);
//    }
//
//    @Test
//    @DisplayName("getMemorialList: page < 0 이면 InvalidPaginationParameterException")
//    void getMemorialList_InvalidPageable_ThrowsException() {
//        // Pageable을 mock으로 생성하고, pageNumber만 음수로 스텁
//        Pageable badPage = mock(Pageable.class);
//        when(badPage.getPageNumber()).thenReturn(-1);
//
//        // 서비스 호출 시 내부 검증 로직에서 예외 발생
//        assertThatThrownBy(() ->
//                memorialService.getMemorialList(
//                        null, null, null,
//                        badPage, SORT_FIELD, DIRECTION
//                )
//        )
//                .isInstanceOf(InvalidPaginationParameterException.class)
//                .hasMessageContaining("유효하지 않은 페이지 번호");
//
//        // 레포지토리가 호출되지 않았음을 검증
//        verify(memorialRepository, never())
//                .findByFilter(any(), any(), any(), any(Pageable.class));
//    }
//
//
//
//    @Test
//    @DisplayName("getMemorialList: 허용되지 않은 sortField 이면 InvalidSearchFieldException")
//    void getMemorialList_InvalidSortField_ThrowsException() {
//        Pageable pageable = PageRequest.of(0, 2);
//        String badField = "badField";
//
//        assertThatThrownBy(() ->
//                memorialService.getMemorialList(
//                        null, null, null,
//                        pageable, badField, DIRECTION
//                )
//        )
//                .isInstanceOf(InvalidSearchFieldException.class)
//                .hasMessageContaining("정렬 불가능한 필드");
//    }
//
//    @Test
//    @DisplayName("getMemorialDetail: 존재하는 donateSeq면 상세 DTO 반환")
//    void getMemorialDetail_ExistingSeq_ReturnsDetailDto() {
//        // given
//        Integer seq = 100;
//        Memorial entity = Memorial.builder()
//                .donateSeq(seq)
//                .donorName("테스트기증자")
//                .anonymityFlag("N")
//                .donateTitle("추모제목")
//                .areaCode("001")
//                .contents("추모 내용")
//                .writer("작성자")
//                .donateDate("20240101")
//                .genderFlag("M")
//                .donateAge(50)
//                .writeTime(LocalDateTime.now())
//                .writerId("user1")
//                .donorBirthdate(LocalDate.parse("19740101", DF))
//                .build();
//
//        when(memorialRepository.findByDonateSeqAndDelFlag(seq, "N"))
//                .thenReturn(Optional.of(entity));
//
//        MemorialReply reply = MemorialReply.builder()
//                .replyContents("테스트 댓글")
//                .build();
//
//        // private 필드인 memorial 에 주입
//        ReflectionTestUtils.setField(reply, "memorial", entity);
//
//        when(replyRepository
//                .findByMemorialDonateSeqAndDelFlagOrderByReplyWriteTimeAsc(seq, "N"))
//                .thenReturn(List.of(reply));
//
//        when(heavenLetterRepository
//                .findByDonateSeqAndDelFlagOrderByWriteTimeDesc(entity, "N"))
//                .thenReturn(List.of());
//
//        // when
//        MemorialDetailResponseDto detail = memorialService.getMemorialDetail(seq);
//
//        // then
//        assertThat(detail.getDonorName()).isEqualTo("테스트기증자");
//        assertThat(detail.getReplies()).hasSize(1);
//        assertThat(detail.getHeavenLetters()).isEmpty();
//
//        verify(memorialRepository).findByDonateSeqAndDelFlag(seq, "N");
//        verify(replyRepository)
//                .findByMemorialDonateSeqAndDelFlagOrderByReplyWriteTimeAsc(seq, "N");
//        verify(heavenLetterRepository)
//                .findByDonateSeqAndDelFlagOrderByWriteTimeDesc(entity, "N");
//    }
//
//    @Test
//    @DisplayName("getMemorialDetail: 존재하지 않는 donateSeq면 예외 발생")
//    void getMemorialDetail_NonExistingSeq_ThrowsNotFound() {
//        // given
//        Integer seq = 999;
//        when(memorialRepository.findByDonateSeqAndDelFlag(seq, "N"))
//                .thenReturn(Optional.empty());
//
//        // when & then
//        assertThatThrownBy(() -> memorialService.getMemorialDetail(seq))
//                .isInstanceOf(MemorialNotFoundException.class)
//                .hasMessageContaining(seq.toString());
//
//        verify(memorialRepository).findByDonateSeqAndDelFlag(seq, "N");
//        verify(replyRepository, never())
//                .findByMemorialDonateSeqAndDelFlagOrderByReplyWriteTimeAsc(anyInt(), anyString());
//        verify(heavenLetterRepository, never())
//                .findByDonateSeqAndDelFlagOrderByWriteTimeDesc(any(), anyString());
//    }
//
//    @Test
//    @DisplayName("createReply: captcha 검증 실패 시 CaptchaVerificationFailedException")
//    void createReply_CaptchaFail_Throws() {
//        var dto = new MemorialReplyCreateRequestDto();
//        dto.setCaptchaToken("bad-token");
//
//        when(captchaVerifier.verifyCaptcha("bad-token"))
//                .thenReturn(false);
//
//        assertThatThrownBy(() -> memorialService.createReply(DONATE_SEQ, dto))
//                .isInstanceOf(CaptchaVerificationFailedException.class);
//
//        verify(replyRepository, never()).save(any());
//    }
//
//    @Test
//    @DisplayName("createReply: 존재하지 않는 memorial이면 MemorialNotFoundException")
//    void createReply_NoMemorial_Throws() {
//        var dto = new MemorialReplyCreateRequestDto();
//        dto.setCaptchaToken("ok");
//        when(captchaVerifier.verifyCaptcha(anyString()))
//                .thenReturn(true);
//        when(memorialRepository.findByDonateSeqAndDelFlag(DONATE_SEQ, "N"))
//                .thenReturn(Optional.empty());
//
//        assertThatThrownBy(() -> memorialService.createReply(DONATE_SEQ, dto))
//                .isInstanceOf(MemorialNotFoundException.class)
//                .hasMessageContaining(DONATE_SEQ.toString());
//    }
//
//    @Test
//    @DisplayName("createReply: 정상 호출 시 CommentResponseDto 반환")
//    void createReply_Success() {
//        // given
//        when(captchaVerifier.verifyCaptcha(anyString()))
//                .thenReturn(true);
//        when(memorialRepository.findByDonateSeqAndDelFlag(DONATE_SEQ, "N"))
//                .thenReturn(Optional.of(dummyMemorial));
//
//        var dto = new MemorialReplyCreateRequestDto();
//        dto.setCaptchaToken("ok");
//        dto.setReplyWriter("writer");
//        dto.setReplyPassword("pass");
//        dto.setReplyContents("hello");
//
//        // save() 시 DB에서 반환될 엔티티
//        var saved = MemorialReply.builder()
//                .replyWriter("writer")
//                .replyPassword("pass")
//                .replyWriterId(null)
//                .replyContents("hello")
//                .replyWriteTime(LocalDateTime.now())
//                .memorial(dummyMemorial)
//                .delFlag("N")
//                .build();
//        ReflectionTestUtils.setField(saved, "replySeq", REPLY_SEQ);
//
//        when(replyRepository.save(any(MemorialReply.class)))
//                .thenReturn(saved);
//
//        // when
//        MemorialReplyResponseDto resp = memorialService.createReply(DONATE_SEQ, dto);
//
//        // then
//        assertThat(resp.getReplySeq()).isEqualTo(REPLY_SEQ);
//        assertThat(resp.getReplyContents()).isEqualTo("hello");
//        verify(replyRepository).save(any());
//    }
//
//    @Test
//    @DisplayName("updateReply: 없는 댓글이면 MemorialReplyNotFoundException")
//    void updateReply_NotFound_Throws() {
//        var dto = new MemorialReplyUpdateRequestDto();
//        dto.setReplyPassword("p");
//        dto.setReplyContents("x");
//        dto.setModifierId("u");
//
//        when(replyRepository.findByMemorialDonateSeqAndReplySeqAndDelFlag(DONATE_SEQ, REPLY_SEQ, "N"))
//                .thenReturn(Optional.empty());
//
//        assertThatThrownBy(() -> memorialService.updateReply(DONATE_SEQ, REPLY_SEQ, dto))
//                .isInstanceOf(MemorialReplyNotFoundException.class)
//                .hasMessageContaining(REPLY_SEQ.toString());
//    }
//
//    @Test
//    @DisplayName("updateReply: 패스워드 불일치 시 InvalidCommentPasscodeException")
//    void updateReply_WrongPass_Throws() {
//        // existing reply
//        var reply = MemorialReply.builder()
//                .replyWriter("w")
//                .replyPassword("correct")
//                .replyWriterId(null)
//                .replyContents("old")
//                .replyWriteTime(LocalDateTime.now())
//                .memorial(dummyMemorial)
//                .delFlag("N")
//                .build();
//        ReflectionTestUtils.setField(reply, "replySeq", REPLY_SEQ);
//
//        when(replyRepository.findByMemorialDonateSeqAndReplySeqAndDelFlag(DONATE_SEQ, REPLY_SEQ, "N"))
//                .thenReturn(Optional.of(reply));
//
//        var dto = new MemorialReplyUpdateRequestDto();
//        dto.setReplyPassword("wrong");
//        dto.setReplyContents("new");
//        dto.setModifierId("u");
//
//        assertThatThrownBy(() -> memorialService.updateReply(DONATE_SEQ, REPLY_SEQ, dto))
//                .isInstanceOf(InvalidCommentPasscodeException.class);
//    }
//
//    @Test
//    @DisplayName("updateReply: 정상 호출 시 내용 변경 후 DTO 반환")
//    void updateReply_Success() {
//        var reply = MemorialReply.builder()
//                .replyWriter("w")
//                .replyPassword("p")
//                .replyWriterId(null)
//                .replyContents("old")
//                .replyWriteTime(LocalDateTime.now())
//                .memorial(dummyMemorial)
//                .delFlag("N")
//                .build();
//        ReflectionTestUtils.setField(reply, "replySeq", REPLY_SEQ);
//
//        when(replyRepository.findByMemorialDonateSeqAndReplySeqAndDelFlag(DONATE_SEQ, REPLY_SEQ, "N"))
//                .thenReturn(Optional.of(reply));
//
//        var dto = new MemorialReplyUpdateRequestDto();
//        dto.setReplyPassword("p");
//        dto.setReplyContents("updated");
//        dto.setModifierId("u");
//
//        MemorialReplyResponseDto resp = memorialService.updateReply(DONATE_SEQ, REPLY_SEQ, dto);
//
//        assertThat(reply.getReplyContents()).isEqualTo("updated");
//        assertThat(resp.getReplySeq()).isEqualTo(REPLY_SEQ);
//        assertThat(resp.getReplyContents()).isEqualTo("updated");
//    }
//
//    @Test
//    @DisplayName("deleteReply: 없는 댓글이면 MemorialReplyNotFoundException")
//    void deleteReply_NotFound_Throws() {
//        var dto = new MemorialReplyDeleteRequestDto();
//        dto.setReplyPassword("p");
//
//        when(replyRepository.findByMemorialDonateSeqAndReplySeqAndDelFlag(DONATE_SEQ, REPLY_SEQ, "N"))
//                .thenReturn(Optional.empty());
//
//        assertThatThrownBy(() -> memorialService.softDeleteReply(DONATE_SEQ, REPLY_SEQ, dto))
//                .isInstanceOf(MemorialReplyNotFoundException.class);
//    }
//
//    @Test
//    @DisplayName("deleteReply: 패스워드 불일치 시 InvalidCommentPasscodeException")
//    void deleteReply_WrongPass_Throws() {
//        var reply = MemorialReply.builder()
//                .replyWriter("w")
//                .replyPassword("correct")
//                .replyWriterId(null)
//                .replyContents("c")
//                .replyWriteTime(LocalDateTime.now())
//                .memorial(dummyMemorial)
//                .delFlag("N")
//                .build();
//        ReflectionTestUtils.setField(reply, "replySeq", REPLY_SEQ);
//
//        when(replyRepository.findByMemorialDonateSeqAndReplySeqAndDelFlag(DONATE_SEQ, REPLY_SEQ, "N"))
//                .thenReturn(Optional.of(reply));
//
//        var dto = new MemorialReplyDeleteRequestDto();
//        dto.setReplyPassword("wrong");
//
//        assertThatThrownBy(() -> memorialService.softDeleteReply(DONATE_SEQ, REPLY_SEQ, dto))
//                .isInstanceOf(InvalidCommentPasscodeException.class);
//    }
//
//    @Test
//    @DisplayName("deleteReply: 정상 호출 시 delFlag='Y'로 변경")
//    void deleteReply_Success() {
//        var reply = MemorialReply.builder()
//                .replyWriter("w")
//                .replyPassword("p")
//                .replyWriterId(null)
//                .replyContents("c")
//                .replyWriteTime(LocalDateTime.now())
//                .memorial(dummyMemorial)
//                .delFlag("N")
//                .build();
//        ReflectionTestUtils.setField(reply, "replySeq", REPLY_SEQ);
//
//        when(replyRepository.findByMemorialDonateSeqAndReplySeqAndDelFlag(DONATE_SEQ, REPLY_SEQ, "N"))
//                .thenReturn(Optional.of(reply));
//
//        var dto = new MemorialReplyDeleteRequestDto();
//        dto.setReplyPassword("p");
//
//        memorialService.softDeleteReply(DONATE_SEQ, REPLY_SEQ, dto);
//
//        assertThat(reply.getDelFlag()).isEqualTo("Y");
//    }
//}
