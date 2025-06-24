package com.ttasum.memorial.domain.repository.notice;

import com.ttasum.memorial.domain.entity.notice.Notice;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class NoticeRepositoryImpl implements NoticeRepositoryCustom {

    private final EntityManager em;

    // 허용 가능한 정렬 필드 집합
    private static final Set<String> ALLOWED_SORT_FIELDS =
            Set.of("writeTime", "title", "readCount");

    /**
     * 조회수 증가 쿼리 실행
     * - 성능 최적화를 위해 DB에서 직접 증가 처리함
     */
    @Override
    @Transactional
    public void increaseHitCount(String boardCode, Integer articleSeq) {
        em.createQuery("""
                UPDATE Notice n
                SET n.readCount = n.readCount + 1
                WHERE n.id.boardCode = :boardCode
                  AND n.id.articleSeq = :articleSeq
            """)
                .setParameter("boardCode", boardCode)
                .setParameter("articleSeq", articleSeq)
                .executeUpdate();
    }

    /**
     * 동적 검색 + 페이징 조회
     * - 읽기 전용 트랜잭션으로 설정하여 JPA가 쓰기 동기화를 최소화
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Notice> searchNotices(
            List<String> boardCodes,
            String searchField,
            String keyword,
            Pageable pageable) {

        // 1) SELECT 절 + WHERE 절 조립
        StringBuilder jpql = new StringBuilder("SELECT n FROM Notice n");
        appendWhereClauses(jpql, boardCodes, searchField, keyword);

        // 2) 동적 정렬 + 검증
        if (pageable.getSort().isSorted()) {
            jpql.append(" ORDER BY ");
            pageable.getSort().forEach(order -> {
                String prop = order.getProperty();
                if (!ALLOWED_SORT_FIELDS.contains(prop)) {
                    throw new IllegalArgumentException("정렬 불가능한 필드: " + prop);
                }
                jpql.append("n.").append(prop)
                        .append(" ").append(order.getDirection())
                        .append(", ");
            });
            jpql.setLength(jpql.length() - 2); // 마지막 ", " 제거
        } else {
            // 기본 정렬: 작성일 내림차순
            jpql.append(" ORDER BY n.writeTime DESC");
        }

        // 3) 데이터 조회 쿼리 + 페이징
        TypedQuery<Notice> query = em.createQuery(jpql.toString(), Notice.class);
        bindParameters(query, boardCodes, keyword);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        List<Notice> content = query.getResultList();

        // 4) COUNT 쿼리
        StringBuilder countJpql = new StringBuilder("SELECT COUNT(n) FROM Notice n");
        appendWhereClauses(countJpql, boardCodes, searchField, keyword);
        TypedQuery<Long> countQuery = em.createQuery(countJpql.toString(), Long.class);
        bindParameters(countQuery, boardCodes, keyword);
        long total = countQuery.getSingleResult();

        return new PageImpl<>(content, pageable, total);
    }

    // WHERE 절을 조립하는 공통 메서드
    private void appendWhereClauses(
            StringBuilder builder,
            List<String> boardCodes,
            String searchField,
            String keyword) {

        builder.append(" WHERE n.delFlag = 'N'");

        if (boardCodes != null && !boardCodes.isEmpty()) {
            builder.append(" AND n.id.boardCode IN :boardCodes");
        }
        if (keyword != null && !keyword.isBlank()) {
            String likeExpr = " LIKE :kw";
            switch (searchField) {
                case "title":
                    builder.append(" AND n.title").append(likeExpr);
                    break;
                case "contents":
                    builder.append(" AND n.contents").append(likeExpr);
                    break;
                default:
                    builder.append(" AND (n.title").append(likeExpr)
                            .append(" OR n.contents").append(likeExpr).append(")");
            }
        }
    }

    // 파라미터 바인딩을 일관되게 처리하는 공통 메서드
    private void bindParameters(
            javax.persistence.Query query,
            List<String> boardCodes,
            String keyword) {

        if (boardCodes != null && !boardCodes.isEmpty()) {
            query.setParameter("boardCodes", boardCodes);
        }
        if (keyword != null && !keyword.isBlank()) {
            query.setParameter("kw", "%" + keyword + "%");
        }
    }
}
