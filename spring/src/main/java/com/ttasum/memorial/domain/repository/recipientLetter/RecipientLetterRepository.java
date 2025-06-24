package com.ttasum.memorial.domain.repository.recipientLetter;

import com.ttasum.memorial.domain.entity.recipientLetter.RecipientLetter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * 수혜자 편지 조회용 Repository
 * - @Where 전역 필터로 delFlag='N' 자동 적용
 */
public interface RecipientLetterRepository extends JpaRepository<RecipientLetter, Integer>, JpaSpecificationExecutor<RecipientLetter> {

    /**
     * 전체 목록 페이징 조회 (delFlag='N')
     */
    Page<RecipientLetter> findAll(Pageable pageable);

    /**
     * 단건 조회 (댓글 포함, delFlag='N')
     */
    @EntityGraph(attributePaths = "comments")
    Optional<RecipientLetter> findWithCommentsByLetterSeq(Integer letterSeq);


//    /**
//     * 댓글(comments)과 꽃(flower)을 함께 fetch join 해서 가져옵니다.
//     */
    @Query("""
        select r
        from RecipientLetter r
        left join fetch r.comments c
        left join fetch r.flower f
        where r.letterSeq = :letterSeq
          and r.delFlag   = 'N'
    """)
    Optional<RecipientLetter> findByLetterSeqWithCommentsAndFlower(
            @Param("letterSeq") Integer letterSeq
    );
}




