package com.example.chatting.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@RedisHash("ChatRoom")
public class ChatRoom implements Serializable {
    @Id
    private Long groupId;
    //private String name;
    //private LocalDateTime createDate;
}
