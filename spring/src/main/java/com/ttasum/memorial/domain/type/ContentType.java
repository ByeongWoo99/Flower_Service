package com.ttasum.memorial.domain.type;

import lombok.Getter;

@Getter
public enum ContentType {
    STORY("story"),
    COMMENT("comment");

    final private String value;
    ContentType(String value) {
        this.value = value;
    }
}
