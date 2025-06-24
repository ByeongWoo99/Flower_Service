package com.ttasum.memorial.domain.repository.donationStory;

import com.ttasum.memorial.domain.entity.donationStory.DonationStory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

/**
 * 기증후 스토리 게시글을 위한 JPA Repository 인터페이스.
 * Spring Data JPA의 JpaRepository를 상속받아 CRUD 기능을 자동 제공.
 */
public interface DonationStoryRepository extends JpaRepository<DonationStory, Integer>,
        DonationStoryRepositoryCustom {
    @Query("""
        select d
        from DonationStory d
        left join fetch d.flower f
        where d.id = :id
          and d.delFlag = :delFlag
    """)

    Optional<DonationStory> findByIdAndDelFlag(Integer id, String delFlag);

    /**
     * 삭제되지 않은 스토리만 조회 (del_flag = 'N')
     * SELECT * FROM tb25  _420_donation_story
     * WHERE del_flag = 'N'
     * ORDER BY write_time DESC
     * LIMIT ?, ?
     */
    Page<DonationStory> findByDelFlagOrderByWriteTimeDesc(String delFlag, Pageable pageable);
}
