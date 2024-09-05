package com.example.stock_system.account.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class AccountPayment {

    private int teamId;
    private int paymentMoney;
    private List<Integer> users;

    public AccountPayment(int teamId, int paymentMoney, List<Integer> users) {
        this.teamId = teamId;
        this.paymentMoney = paymentMoney;
        this.users = users;
    }


}
