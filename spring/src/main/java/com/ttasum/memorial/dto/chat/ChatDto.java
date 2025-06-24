package com.ttasum.memorial.dto.chat;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

/**
 * 사용자 입력과 관련된 데이터를 담는 DTO
 */
@Getter
@ToString
public class ChatDto {
    private final String question;

    @JsonCreator
    public ChatDto(@JsonProperty("question") String question) {
        this.question = question;
    }
}