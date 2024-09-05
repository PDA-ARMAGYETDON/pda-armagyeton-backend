package com.example.alarm.firebase.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@NoArgsConstructor
@Getter
public class FcmTokenResponseDto {

    private int userId;
    private ArrayList<Integer> teamIds;
    private String fcmToken;

    @JsonCreator
    public FcmTokenResponseDto(
            @JsonProperty("userId") int userId,
            @JsonProperty("teamIds") ArrayList<Integer> teamIds,
            @JsonProperty("fcmToken") String fcmToken) {
        this.userId = userId;
        this.teamIds = teamIds;
        this.fcmToken = fcmToken;
    }
}
