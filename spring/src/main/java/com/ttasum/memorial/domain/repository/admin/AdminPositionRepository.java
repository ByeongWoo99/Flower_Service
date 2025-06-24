package com.ttasum.memorial.domain.repository.admin;

import com.ttasum.memorial.domain.entity.admin.AdminPosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.ArrayList;

public interface AdminPositionRepository extends JpaRepository<AdminPosition, String> {

    @Query("SELECT a FROM AdminPosition a WHERE a.positionCode = :position")
    AdminPosition findAdminPositionByPositionCode(@Param("position") String position);
}
