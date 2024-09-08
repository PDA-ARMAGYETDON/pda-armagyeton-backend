package com.example.group_investment.team.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PayFail {
    private int teamId;

    private List<Integer> userId;

    public PayFail(int teamId, List<Integer> userId) {
        this.teamId = teamId;
        this.userId = userId;
    }
}
