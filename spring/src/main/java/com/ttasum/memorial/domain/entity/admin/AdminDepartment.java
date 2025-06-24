package com.ttasum.memorial.domain.entity.admin;

import lombok.Getter;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Entity
@Table(name = "tb25_840_org_chart")
public class AdminDepartment {
    @Id
    @Column(name = "org_seq", nullable = false)
    private Integer id;

    @Size(max = 60)
    @Column(name = "hq_name", length = 60)
    private String hqName;

    @Size(max = 60)
    @Column(name = "dept1_name", length = 60)
    private String dept1Name;

    @Size(max = 60)
    @Column(name = "dept2_name", length = 60)
    private String dept2Name;

    @Size(max = 3000)
    @Column(name = "job_desc", length = 3000)
    private String jobDesc;

    @Size(max = 20)
    @Column(name = "tel_no", length = 20)
    private String telNo;

    @OneToMany(mappedBy = "departmentCode")
    private Set<User> users = new LinkedHashSet<>();

}