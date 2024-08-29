package com.example.stock_system.account.dto;

import com.example.stock_system.account.Account;
import lombok.Builder;
import lombok.Getter;

@Getter
public class AccountDto {
    private String accountNumber;
    private int deposit;
    private int totalEvluAmt;
    private int totalPchsAmt;
    private int totalEvluPfls;
    private double totalEvluPflsRt;

    public Account toEntity() {
        return Account.builder()
                .accountNumber(this.accountNumber)
                .deposit(this.deposit)
                .totalEvluAmt(this.totalEvluAmt)
                .totalPchsAmt(this.totalPchsAmt)
                .totalEvluPfls(this.totalEvluPfls)
                .totalEvluPflsRt(this.totalEvluPflsRt)
                .build();
    }


    @Builder
    public AccountDto(String accountNumber, int deposit, int totalEvluAmt, int totalPchsAmt, int totalEvluPfls, double totalEvluPflsRt) {
        this.accountNumber = accountNumber;
        this.deposit = deposit;
        this.totalEvluAmt = totalEvluAmt;
        this.totalPchsAmt = totalPchsAmt;
        this.totalEvluPfls = totalEvluPfls;
        this.totalEvluPflsRt = totalEvluPflsRt;
    }


}
