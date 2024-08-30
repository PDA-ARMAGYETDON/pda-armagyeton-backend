package com.example.alarm.firebase.dto;

import lombok.Getter;

@Getter
public class FcmTokenRequestDto {

    private String userId;
    
    private String fcmToken;
}
