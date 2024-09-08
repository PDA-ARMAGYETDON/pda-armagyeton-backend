package com.example.group_investment.team.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class FirstPayment {

    private int teamId;
    private int paymentMoney;
    private List<Integer> users;

    public FirstPayment(int teamId,int paymentMoney, List<Integer> users){
        this.teamId = teamId;
        this.paymentMoney = paymentMoney;
        this.users = users;
    }
}
