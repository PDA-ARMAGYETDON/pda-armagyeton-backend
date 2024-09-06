package com.example.chatting.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ChatMessageResponse {
    private int senderId;
    private String name;
    private String message;
    private LocalDateTime createdAt;
}
