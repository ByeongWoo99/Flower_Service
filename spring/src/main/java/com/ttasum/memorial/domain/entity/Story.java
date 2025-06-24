package com.ttasum.memorial.domain.entity;

import com.ttasum.memorial.domain.type.BoardType;
import lombok.Getter;
import lombok.Setter;

// 게시글 공통 조상
@Getter
@Setter
public abstract class Story extends Contents{
    public abstract Integer getId();
    public abstract BoardType getBoardType();
    public abstract String getContents();
    public abstract void setDelFlag(String delFlag);
}