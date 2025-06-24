package com.ttasum.memorial.domain.entity.admin;

import lombok.Getter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Entity
@Table(name = "admin_position")
public class AdminPosition {
    @Id
    @Size(max = 50)
    @Column(name = "position_code", nullable = false, length = 50)
    private String positionCode;

    @Size(max = 50)
    @NotNull
    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @NotNull
    @Column(name = "salary", nullable = false)
    private Integer salary;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_code")
    private AdminPosition managerCode;

    @NotNull
    @Column(name = "active_flag", nullable = false)
    private Byte activeFlag;

    @OneToMany(mappedBy = "position")
    private List<User> employees;
}