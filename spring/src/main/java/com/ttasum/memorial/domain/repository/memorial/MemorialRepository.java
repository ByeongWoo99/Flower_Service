package com.ttasum.memorial.domain.repository.memorial;

import com.ttasum.memorial.domain.entity.memorial.Memorial;
import com.ttasum.memorial.dto.Main.RememberanceMainDto;
import com.ttasum.memorial.dto.memorial.response.MemorialResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemorialRepository extends JpaRepository<Memorial, Integer>, JpaSpecificationExecutor<Memorial> {

    @Query("""
            SELECT new com.ttasum.memorial.dto.memorial.response.MemorialResponseDto(
                m.id,
                m.donorName,
                m.anonymityFlag,
                m.genderFlag,
                m.donateAge,
                m.donateDate,
                COUNT(r.commentSeq),
                m.donorBirthdate
            )
            FROM Memorial m
            LEFT JOIN m.replies r
            WHERE m.delFlag = 'N'
              AND (:donorName IS NULL OR :donorName = '' OR m.donorName LIKE CONCAT('%', :donorName, '%'))
              AND (
                   (:startDate IS NULL OR :endDate IS NULL)
                OR (m.donateDate >= :startDate AND m.donateDate <= :endDate)
              )
            GROUP BY
                m.id,
                m.donorName,
                m.anonymityFlag,
                m.genderFlag,
                m.donateAge,
                m.donateDate,
                m.donorBirthdate
            """)
    Page<MemorialResponseDto> findByFilter(
            @Param("donorName") String donorName,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate,
            Pageable pageable
    );

    Optional<Memorial> findByIdAndDelFlag(Integer donateSeq, String delFlag);

    @Query("SELECT new com.ttasum.memorial.dto.Main.RememberanceMainDto(r.donorName, r.genderFlag, r.donateAge) " +
            "FROM Memorial r " +
            "WHERE r.delFlag = 'N' " +
            "ORDER BY r.modifyTime DESC")
    List<RememberanceMainDto> findRecentRememberance(Pageable pageable);

}
