package com.example.group_investment.user.dto;

import lombok.Getter;

import java.util.ArrayList;

@Getter
public class FcmTokenDeleteDto {
    private int userId;
    private ArrayList<Integer> teamList;

    public FcmTokenDeleteDto(int userId, ArrayList<Integer> teamList) {
        this.userId = userId;
        this.teamList = teamList;
    }
}
