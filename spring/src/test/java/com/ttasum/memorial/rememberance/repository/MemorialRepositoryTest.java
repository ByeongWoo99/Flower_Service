//package com.ttasum.memorial.rememberance.repository;
//
//import com.ttasum.memorial.domain.entity.memorial.Memorial;
//import com.ttasum.memorial.domain.entity.memorial.MemorialReply;
//import com.ttasum.memorial.domain.repository.memorial.MemorialRepository;
//import com.ttasum.memorial.dto.memorial.response.MemorialResponseDto;
//import io.github.cdimascio.dotenv.Dotenv;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//import org.springframework.transaction.annotation.Transactional;
//
//import javax.persistence.EntityManager;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.*;
//
//@SpringBootTest
//@Transactional
//class MemorialRepositoryTest {
//
//    @Autowired
//    private MemorialRepository memorialRepository;
//
//    @Autowired
//    private EntityManager em;
//
//    @BeforeAll
//    static void loadEnv() {
//        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
//        System.setProperty("DB_PW", dotenv.get("DB_PW"));
//    }
//
//    @Test
//    @DisplayName("findByDonateSeqAndDelFlag: delFlag='N' 이면 Optional.of 반환")
//    void findByDonateSeqAndDelFlag_FlagN_ReturnsEntity() {
//        // given
//        Memorial m = Memorial.builder()
//                .donateSeq(null)
//                .donorName("김테스트")
//                .anonymityFlag("N")
//                .donateTitle("제목")
//                .areaCode("001")
//                .contents("내용")
//                .fileName(null)
//                .orgFileName(null)
//                .writer("tester")
//                .donateDate("20240101")
//                .genderFlag("M")
//                .donateAge(30)
//                .writerId("u1")
//                .donorBirthdate(LocalDate.of(1990,1,1))
//                .delFlag("N")
//                .build();
//        // persist -> generate ID
//        em.persist(m);
//        em.flush();
//        Integer seq = m.getDonateSeq();
//
//        // when
//        Optional<Memorial> found = memorialRepository.findByDonateSeqAndDelFlag(seq, "N");
//
//        // then
//        assertThat(found)
//                .isPresent()
//                .get()
//                .extracting(Memorial::getDonorName)
//                .isEqualTo("김테스트");
//    }
//
//    @Test
//    @DisplayName("findByDonateSeqAndDelFlag: delFlag!='N' 이면 Optional.empty")
//    void findByDonateSeqAndDelFlag_FlagY_ReturnsEmpty() {
//        // given
//        Memorial m = Memorial.builder()
//                .donateSeq(null)
//                .donorName("박테스트")
//                .anonymityFlag("N")
//                .donateTitle("제목")
//                .areaCode("001")
//                .contents("내용")
//                .fileName(null)
//                .orgFileName(null)
//                .writer("tester")
//                .donateDate("20240101")
//                .genderFlag("F")
//                .donateAge(25)
//                .writerId("u2")
//                .donorBirthdate(LocalDate.of(1995,5,5))
//                .delFlag("Y")
//                .build();
//        em.persist(m);
//        em.flush();
//
//        // when
//        Optional<Memorial> found = memorialRepository.findByDonateSeqAndDelFlag(m.getDonateSeq(), "N");
//
//        // then
//        assertThat(found).isEmpty();
//    }
//
//    @Test
//    @DisplayName("findByFilter: donorName 조건으로 필터링")
//    void findByFilter_NameFilter_ReturnsMatching() {
//        // given: 세 건 중 하나만 '홍' 포함
//        Memorial m1 = Memorial.builder()
//                .donorName("홍길동")
//                .anonymityFlag("N")
//                .donateTitle("T1")
//                .areaCode("001")
//                .contents("C1")
//                .fileName(null)
//                .orgFileName(null)
//                .writer("u1")
//                .donateDate("20240101")
//                .genderFlag("M")
//                .donateAge(50)
//                .writerId("u1")
//                .delFlag("N")
//                .donorBirthdate(LocalDate.of(1970,1,1))
//                .build();
//        Memorial m2 = Memorial.builder()
//                .donorName("김철수")
//                .anonymityFlag("N")
//                .donateTitle("T2")
//                .areaCode("002")
//                .contents("C2")
//                .fileName(null)
//                .orgFileName(null)
//                .writer("u2")
//                .donateDate("20240202")
//                .genderFlag("M")
//                .donateAge(45)
//                .writerId("u2")
//                .delFlag("N")
//                .donorBirthdate(LocalDate.of(1975,2,2))
//                .build();
//        em.persist(m1);
//        em.persist(m2);
//
//        // 한 건에 댓글 2개 등록
//        MemorialReply r1 = MemorialReply.builder()
//                .memorial(m1)
//                .replyContents("댓글1")
//                .replyWriteTime(LocalDateTime.now())
//                .build();
//        em.persist(r1);
//        MemorialReply r2 = MemorialReply.builder()
//                .memorial(m1)
//                .replyContents("댓글2")
//                .replyWriteTime(LocalDateTime.now())
//                .build();
//        em.persist(r2);
//
//        em.flush();
//
//        Pageable pageable = PageRequest.of(0, 10, Sort.by("donateSeq").descending());
//
//        // when
//        Page<MemorialResponseDto> page = memorialRepository.findByFilter(
//                "홍", null, null, pageable);
//
//        // then
//        MemorialResponseDto dto = page.getContent().get(0);
//        assertThat(dto.getDonorName()).isEqualTo("홍길동");
//        // 댓글 2건일 때 COUNT(r.replySeq) 검증
//        assertThat(dto.getCommentCount()).isEqualTo(2L);
//    }
//}
