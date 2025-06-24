package com.ttasum.memorial.domain.repository.admin;

import com.ttasum.memorial.domain.entity.admin.AdminAuthority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.ArrayList;

public interface AdminAuthorityRepository extends JpaRepository<AdminAuthority,Integer> {

    @Query("SELECT a FROM AdminAuthority a WHERE a.authorityCode = :roles")
    AdminAuthority findadminAuthorityByAuthorityCode(@Param("roles") String roles);
}
