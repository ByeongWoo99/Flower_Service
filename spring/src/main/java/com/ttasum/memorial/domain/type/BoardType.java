package com.ttasum.memorial.domain.type;

import lombok.Getter;

@Getter
public enum BoardType {
    DONATION("donation"),
    HEAVEN("heaven"),
    RECIPIENT("recipient"),
    REMEMBRANCE("remember"),;

    private final String type;

    BoardType(String type) {
        this.type = type;
    }
}
