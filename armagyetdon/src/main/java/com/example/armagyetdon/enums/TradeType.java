package com.example.armagyetdon.enums;

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
