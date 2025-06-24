package com.ttasum.memorial.domain.repository.donationStory;

import com.ttasum.memorial.domain.entity.donationStory.DonationStory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class DonationStoryRepositoryImpl implements DonationStoryRepositoryCustom {

    private final EntityManager em;

    // TODO: 기증후 스토리는 조회수 안쓸듯?
    // 1) 허용 가능한 정렬 필드 (기증후스토리 기준)
    private static final Set<String> ALLOWED_SORT_FIELDS =
            Set.of("writeTime", "storyTitle", "readCount");


    /**
     * 동적 검색 + 페이징 조회 (기증후스토리)
     *
     * @param searchField 검색 대상 필드 ("title", "contents", "all")
     * @param keyword     검색어
     * @param pageable    페이징 정보
     * @return Page<DonationStory>
     */
    @Transactional(readOnly = true)
    @Override
    public Page<DonationStory> searchStories(
            String searchField,
            String keyword,
            Pageable pageable) {

        // 1) SELECT 절 + WHERE 절 조립
        StringBuilder jpql = new StringBuilder("SELECT ds FROM DonationStory ds");
        appendWhereClauses(jpql, searchField, keyword);

        // 2) 동적 정렬 + 검증
        if (pageable.getSort().isSorted()) {
            jpql.append(" ORDER BY ");
            pageable.getSort().forEach(order -> {
                String prop = order.getProperty();
                if (!ALLOWED_SORT_FIELDS.contains(prop)) {
                    throw new IllegalArgumentException("정렬 불가능한 필드: " + prop);
                }
                jpql.append("ds.").append(prop)
                        .append(" ").append(order.getDirection())
                        .append(", ");
            });
            // 마지막에 붙은 ", " 제거
            jpql.setLength(jpql.length() - 2);
        } else {
            // 기본 정렬: 작성일 내림차순
            jpql.append(" ORDER BY ds.writeTime DESC");
        }

        // 3) 데이터 조회 쿼리 + 페이징
        TypedQuery<DonationStory> query = em.createQuery(jpql.toString(), DonationStory.class);
        bindParameters(query, keyword);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        List<DonationStory> content = query.getResultList();

        // 4) COUNT 쿼리
        StringBuilder countJpql = new StringBuilder("SELECT COUNT(ds) FROM DonationStory ds");
        appendWhereClauses(countJpql, searchField, keyword);
        TypedQuery<Long> countQuery = em.createQuery(countJpql.toString(), Long.class);
        bindParameters(countQuery, keyword);
        long total = countQuery.getSingleResult();

        return new PageImpl<>(content, pageable, total);
    }

    // WHERE 절 조립 로직
    private void appendWhereClauses(
            StringBuilder builder,
            String searchField,
            String keyword) {

        // delFlag는 엔티티 필드명이 delFlag
        builder.append(" WHERE ds.delFlag = 'N'");

        if (keyword != null && !keyword.isBlank()) {
            String likeExpr = " LIKE :kw";
            switch (searchField) {
                case "title":
                    // 엔티티 필드명: title
                    builder.append(" AND ds.title").append(likeExpr);
                    break;
                case "contents":
                    // 엔티티 필드명: contents
                    builder.append(" AND ds.contents").append(likeExpr);
                    break;
                default:
                    // 전체(제목+내용)
                    builder.append(" AND (ds.title").append(likeExpr)
                            .append(" OR ds.contents").append(likeExpr).append(")");
            }
        }
    }

    // 파라미터 바인딩 로직
    private void bindParameters(
            javax.persistence.Query query,
            String keyword) {

        if (keyword != null && !keyword.isBlank()) {
            query.setParameter("kw", "%" + keyword + "%");
        }
    }
}
