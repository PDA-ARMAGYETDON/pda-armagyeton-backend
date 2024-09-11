package com.example.alarm.firebase.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChatAlarmDto {
    private int id;
    private int teamId;
    private int userId;
    private String name;
    private String message;
}