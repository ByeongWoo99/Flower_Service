package com.ttasum.memorial.donationstory.repository;

import com.ttasum.memorial.domain.entity.donationStory.DonationStory;
import com.ttasum.memorial.domain.repository.donationStory.DonationStoryRepository;
import com.ttasum.memorial.domain.repository.donationStory.DonationStoryRepositoryImpl;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;


import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class DonationStoryRepositoryImplTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private DonationStoryRepository donationStoryRepository;

    @BeforeAll
    static void loadEnv() {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        System.setProperty("DB_PW", dotenv.get("DB_PW"));
    }

    /**
     * 간편히 쓰기 위해 만든 헬퍼.
     *
     * @param title    제목
     * @param contents 내용
     * @param deleted  true면 delFlag='Y' 상태로 만든다.
     */
    private DonationStory persistStory(String title, String contents, boolean deleted) {
        DonationStory story = DonationStory.builder()
                .areaCode("A01")
                .title(title)
                .donorName("기증자")
                .passcode("pw")
                .writer("작성자")
                .anonymityFlag("N")
                .readCount(0)
                .contents(contents)
                .fileName(null)
                .originalFileName(null)
                .writerId(null)
                .modifierId(null)
                .build();
        em.persist(story);
        em.flush();

        if (deleted) {
            // 엔티티의 delete() 메서드로 delFlag='Y' 처리
            story.delete("test-modifier");
            em.flush();
        }

        return story;
    }

    @Test
    @DisplayName("검색어 없이 전체 조회 시 delFlag='N'인 것만 반환")
    void findAllWithoutKeyword_ReturnsOnlyNonDeleted() {
        // given
        persistStory("제목1", "내용1", false);
        persistStory("제목2", "내용2", true);
        Pageable page = PageRequest.of(0, 10);

        // when
        Page<DonationStory> result = donationStoryRepository.searchStories("all", null, page);

        // then
        assertThat(result.getContent())
                .allSatisfy(ds -> assertThat(ds.getDelFlag()).isEqualTo("N"));
    }

    @Test
    @DisplayName("검색 대상 'title' 키워드 검색 시 제목에만 매칭")
    void searchByTitle_MatchesTitleOnly() {
        // given
        persistStory("Spring Data", "JPA 내용", false);
        persistStory("Test", "Spring Framework 내용", false);
        Pageable page = PageRequest.of(0, 10);

        // when
        Page<DonationStory> result = donationStoryRepository.searchStories("title", "Spring", page);

        // then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getTitle()).contains("Spring");
    }

    @Test
    @DisplayName("검색 대상 'contents' 키워드 검색 시 내용에만 매칭")
    void searchByContents_MatchesContentsOnly() {
        // given
        persistStory("제목1", "Java 내용", false);
        persistStory("제목2", "Python 내용", false);
        Pageable page = PageRequest.of(0, 10);

        // when
        Page<DonationStory> result = donationStoryRepository.searchStories("contents", "Python", page);

        // then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getContents()).contains("Python");
    }

    @Test
    @DisplayName("검색 대상 'all' 키워드 검색 시 제목 또는 내용에 매칭")
    void searchByAll_MatchesTitleOrContents() {
        // given
        persistStory("Java", "내용 없음", false);
        persistStory("제목 없음", "JavaScript 내용", false);
        persistStory("Python", "내용 없음", false);
        Pageable page = PageRequest.of(0, 10);

        // when
        Page<DonationStory> result = donationStoryRepository.searchStories("all", "Java", page);

        // then
        assertThat(result.getTotalElements()).isEqualTo(2);
        List<String> titles = result.map(DonationStory::getTitle).getContent();
        assertThat(titles).containsExactlyInAnyOrder("Java", "제목 없음");
    }

    @Test
    @DisplayName("정렬 필드를 지정하면 해당 순서로 반환")
    void sortByWriteTimeDescending_Works() {
        // given
        DonationStory older = persistStory("Old", "c", false);
        // 잠시 시간 차이를 두려면 writeTime을 직접 조작하거나, Thread.sleep() 등 사용
        DonationStory newer = persistStory("New", "b", false);

        Pageable page = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "writeTime"));

        // when
        Page<DonationStory> result = donationStoryRepository.searchStories("all", null, page);

        // then
        assertThat(result.getContent().get(0).getId()).isEqualTo(newer.getId());
        assertThat(result.getContent().get(1).getId()).isEqualTo(older.getId());
    }

    @Test
    @DisplayName("허용되지 않은 정렬 필드를 지정하면 예외 발생")
    void sortByNotAllowedField_Throws() {
        // given
        persistStory("제목", "내용", false);
        Pageable page = PageRequest.of(0, 10, Sort.by("notAllowed"));

        // when & then
        assertThatThrownBy(() ->
                donationStoryRepository.searchStories("all", null, page)
        )
                // 원래 IllegalArgumentException 을 감싸는 Spring의 래퍼 예외를 기대
                .isInstanceOf(InvalidDataAccessApiUsageException.class)
                .hasMessageContaining("정렬 불가능한 필드: notAllowed");
    }
}
