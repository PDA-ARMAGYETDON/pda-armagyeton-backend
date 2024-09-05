package com.example.group_investment.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;

@Getter
@AllArgsConstructor
public class FcmTokenResponseDto {

    private int userId;

    private ArrayList<Integer> teamIds;

    private String fcmToken;

    @Override
    public String toString() {
        return "FcmTokenResponseDto{" +
                "userId=" + userId +
                ", teamIds=" + teamIds +
                ", fcmToken='" + fcmToken + '\'' +
                '}';
    }

}
