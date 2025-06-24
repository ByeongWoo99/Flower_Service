package com.ttasum.memorial.service.chat;

import com.ttasum.memorial.dto.chat.ChatApiResponse;
import com.ttasum.memorial.dto.chat.ChatDto;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface ChatService {
    Mono<ResponseEntity<ChatApiResponse>> getLLMResponse(ChatDto inputMsg);
}