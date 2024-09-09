package com.example.group_investment.tradeOffer.dto;

import lombok.Getter;

@Getter
public class VoteStockToAlarmDto {
    private int teamId;
    private String teamName;

    public VoteStockToAlarmDto(int teamId, String teamName) {
        this.teamId = teamId;
        this.teamName = teamName;
    }
}
