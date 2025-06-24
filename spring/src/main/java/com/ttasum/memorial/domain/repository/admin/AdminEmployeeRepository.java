package com.ttasum.memorial.domain.repository.admin;

import com.ttasum.memorial.domain.entity.admin.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AdminEmployeeRepository extends JpaRepository<User,Integer> {
    @Query("SELECT u FROM User u where u.id = :id")
    User findAdminEmployeeById(@Param("id") String id);

    @Query("SELECT u FROM User u WHERE u.id = :id AND u.activeFlag = :activeFlag")
    User findAdminEmployeeByIdAndActiveFlag(@Param("id") String id, @Param("activeFlag") byte y);
}
