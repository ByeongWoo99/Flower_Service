package com.ttasum.memorial.domain.entity;

import lombok.Getter;
import lombok.Setter;

// 댓글 공통 조상
@Getter
@Setter
public abstract class Comment extends Contents {
    public abstract Integer getCommentSeq();
    public abstract Story getLetterSeq();
    public abstract String getContents();
    public abstract void setDelFlag(String delFlag);
}
