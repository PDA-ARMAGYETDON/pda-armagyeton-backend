package com.example.alarm.firebase.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ChatAlarmDto {
    private int id;
    private int teamId;
    private int userId;
    private String name;
    private String message;
    private LocalDateTime createdAt;
}