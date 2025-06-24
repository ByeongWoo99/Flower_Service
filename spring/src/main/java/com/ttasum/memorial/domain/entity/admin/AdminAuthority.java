package com.ttasum.memorial.domain.entity.admin;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Entity
@Table(name = "admin_authority")
public class AdminAuthority {
    @Id
    @Size(max = 50)
    @Column(name = "authority_code", nullable = false, length = 50)
    private String authorityCode;

    @Size(max = 50)
    @NotNull
    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Size(max = 300)
    @Column(name = "note", length = 300)
    private String note;

    @NotNull
    @Column(name = "authority_read", nullable = false)
    private Byte authorityRead;

    @NotNull
    @Column(name = "write_authority", nullable = false)
    private Byte writeAuthority;

    @NotNull
    @Column(name = "update_authority", nullable = false)
    private Byte updateAuthority;

    @NotNull
    @Column(name = "delete_authority", nullable = false)
    private Byte deleteAuthority;

    @NotNull
    @Column(name = "active_flag", nullable = false)
    private Byte activeFlag;

}