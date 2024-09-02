package com.example.group_investment.enums;

import lombok.Getter;

@Getter
public enum TradeType {
    BUY("매수"),
    SELL("매도");

    private final String message;

    TradeType(String message){
        this.message = message;
    }
}
