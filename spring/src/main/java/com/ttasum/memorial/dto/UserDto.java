// 클라이언트 요청/응답용 DTO
package com.ttasum.memorial.dto;

import com.ttasum.memorial.domain.entity.admin.AdminAuthority;
import com.ttasum.memorial.domain.entity.admin.AdminPosition;
import com.ttasum.memorial.domain.entity.admin.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private String id;
    private String password;
    private String name;
    private Integer age;
    private String gender;
    private String email;
    private String phoneNumber;
    private LocalDate hireDate;
    private Byte activeFlag;
    private String roles;
    private String position;
    private Integer departmentCode;

//    public User toEntity() {
//        return User.builder()
//                .id(this.id)
//                .password(this.password)
//                .name(this.name)
//                .age(this.age)
//                .gender(this.gender)
//                .email(this.email)
//                .phoneNumber(this.phoneNumber)
//                .hireDate(LocalDate.now())
//                .activeFlag((byte) 'Y')
//                .roles(new AdminAuthority())
//                .position(new AdminPosition())
//                .departmentCode().build();
//    }
}
