package com.example.stock_system.account.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class GetTeamAccountResponse {

    private String accountNumber;
    private int totalPchsAmt;
    private int totalEvluAmt;
    private int totalEvluPfls;
    private double totalEvluPflsRt;
    private int deposit;
    private int totalAsset;


    public GetTeamAccountResponse() {
    }

    @Builder
    public GetTeamAccountResponse(String accountNumber, int totalPchsAmt, int totalEvluAmt, int totalEvluPfls, double totalEvluPflsRt, int deposit, int totalAsset) {
        this.accountNumber = accountNumber;
        this.totalPchsAmt = totalPchsAmt;
        this.totalEvluAmt = totalEvluAmt;
        this.totalEvluPfls = totalEvluPfls;
        this.totalEvluPflsRt = totalEvluPflsRt;
        this.deposit = deposit;
        this.totalAsset = totalAsset;

    }
}
