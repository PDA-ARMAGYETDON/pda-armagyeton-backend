package com.example.stock_system.account.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class FirstPayment {

    private int teamId;
    private String teamName;
    private int paymentMoney;
    private List<Integer> users;

    public FirstPayment(int teamId,String teamName,int paymentMoney, List<Integer> users){
        this.teamId = teamId;
        this.teamName = teamName;
        this.paymentMoney = paymentMoney;
        this.users = users;
    }

    public FirstPayment(){

    }
}
