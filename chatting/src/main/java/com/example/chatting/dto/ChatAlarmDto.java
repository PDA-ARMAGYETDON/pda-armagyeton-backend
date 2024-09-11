package com.example.chatting.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChatAlarmDto {
    private int id;
    private int teamId;
    private int userId;
    private String name;
    private String message;
    private LocalDateTime createdAt;
}