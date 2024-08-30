package com.example.alarm.firebase.dto;

import lombok.Getter;

@Getter
public class FcmTokenRequestDto {

    private int userId;

    private String fcmToken;
}
