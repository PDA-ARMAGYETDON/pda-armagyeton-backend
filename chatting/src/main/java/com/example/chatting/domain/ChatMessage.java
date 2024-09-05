package com.example.chatting.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;
import java.time.LocalDateTime;

@Builder(toBuilder = true)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("ChatMessage")
public class ChatMessage implements Serializable {
    @Id
    private int id;

    @Indexed
    private int teamId;
    private int userId;
    private String name;
    private String message;
    private LocalDateTime createdAt;
}
