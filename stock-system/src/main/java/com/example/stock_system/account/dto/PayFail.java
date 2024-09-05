package com.example.stock_system.account.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class PayFail {
    private int teamId;

    private List<Integer> userId;

    public PayFail(int teamId, List<Integer> userId) {
        this.teamId = teamId;
        this.userId = userId;
    }
}
