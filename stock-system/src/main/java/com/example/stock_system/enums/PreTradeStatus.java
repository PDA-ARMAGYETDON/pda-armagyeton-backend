package com.example.stock_system.enums;

public enum PreTradeStatus {
    PENDING("예약중"),
    COMPLETED("주문완료"),
    FINISHED("예약만료"),
    ;

    private final String message;

    PreTradeStatus(String message){
        this.message = message;
    }
}
