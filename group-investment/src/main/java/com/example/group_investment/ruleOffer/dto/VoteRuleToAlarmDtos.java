package com.example.group_investment.ruleOffer.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class VoteRuleToAlarmDto {
    private int teamId;
    private String teamName;

    public VoteRuleToAlarmDto(int teamId, String teamName) {
        this.teamId = teamId;
        this.teamName = teamName;
    }
}
