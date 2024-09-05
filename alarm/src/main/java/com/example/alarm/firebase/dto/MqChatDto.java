package com.example.alarm.firebase.dto;

import lombok.Getter;

@Getter
public class MqChatDto {

    private String userId;

    private String teamId;

    private String chatMessage;

    @Override
    public String toString() {
        return "MqChatDto{" +
                "userId='" + userId + '\'' +
                ", teamId='" + teamId + '\'' +
                ", chatMessage='" + chatMessage + '\'' +
                '}';
    }
}
