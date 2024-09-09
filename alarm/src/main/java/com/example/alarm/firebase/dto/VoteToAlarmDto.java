package com.example.alarm.firebase.dto;

import lombok.Getter;

@Getter
public class VoteToAlarmDto {
    private int teamId;
    private String teamName;

    public VoteToAlarmDto(int teamId, String teamName) {
        this.teamId = teamId;
        this.teamName = teamName;
    }
}
