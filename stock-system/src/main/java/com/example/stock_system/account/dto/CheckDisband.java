package com.example.stock_system.account.dto;

import lombok.Getter;

@Getter
public class CheckDisband {

    private int teamId;
    private double maxLossRt;
    private double maxProfitRt;

    public CheckDisband(int teamId,double maxLossRt, double maxProfitRt) {
        this.teamId = teamId;
        this.maxLossRt = maxLossRt;
        this.maxProfitRt = maxProfitRt;
    }

    public CheckDisband(){

    }
}
