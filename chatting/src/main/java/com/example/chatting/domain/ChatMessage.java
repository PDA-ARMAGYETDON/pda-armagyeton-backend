package com.example.chatting.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@RedisHash("ChatMessage")
public class ChatMessage implements Serializable {
    @Id
    private Long id;

    @Indexed
    private Long groupId;
    private Long userId;
    private String message;
}
