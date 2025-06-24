package com.ttasum.memorial.domain.repository.admin;

import com.ttasum.memorial.domain.entity.admin.AdminDepartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AdminDepartmentRepository extends JpaRepository<AdminDepartment, Integer> {

    @Query("SELECT a FROM AdminDepartment a WHERE a.id = :id")
    AdminDepartment findAdminDepartmentById(@Param("id") Integer departmentCode);
}
