package com.ttasum.memorial.domain.entity.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "admin_employee")
public class User
        implements UserDetails {
//    {
    @Id
    @Size(max = 30)
    @Column(name = "id", nullable = false, length = 30)
    private String id;

    @NotNull
    @Column(name = "password", nullable = false)
    private String password;

    @Size(max = 20)
    @NotNull
    @Column(name = "name", nullable = false, length = 20)
    private String name;

    @NotNull
    @Column(name = "age", nullable = false)
    private Integer age;

    @Size(max = 5)
    @NotNull
    @Column(name = "gender", nullable = false, length = 5)
    private String gender;

    @Size(max = 100)
    @Column(name = "email", length = 100)
    private String email;

    @Size(max = 50)
    @NotNull
    @Column(name = "phone_number", nullable = false, length = 50)
    private String phoneNumber;

    @NotNull
    @Column(name = "hire_date", nullable = false)
    private LocalDate hireDate;

    @NotNull
    @Column(name = "active_flag", nullable = false)
    private Byte activeFlag;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "authority_code")
    private AdminAuthority roles;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_code")
    private AdminPosition position;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "department_code", nullable = false)
    private AdminDepartment departmentCode;

    // UserDetails 구현 메서드
    // 사용자가 가지고 있는 권한 목록
    // 권한 이름에 "ROLE_" 접두사를 붙이는 게 관례이자 필요 조건
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(roles.getAuthorityCode()));
    }

    // 사용자의 식별 아이디
    @Override
    public String getUsername() {
        return this.id;
    }

    // 계정 만료 여부
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // 계정 잠금 여부
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // 패스워드 만료 여부
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // 계정 사용 여부
    @Override
    public boolean isEnabled() {
        return this.activeFlag == 'Y';
    }
}