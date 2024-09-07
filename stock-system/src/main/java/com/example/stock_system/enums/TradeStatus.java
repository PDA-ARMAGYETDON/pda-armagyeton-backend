package com.example.stock_system.enums;

public enum TradeStatus {
    PENDING("거래대기"),
    COMPLETED("거래완료");

    private final String message;

    TradeStatus(String message) {
        this.message = message;
    }
}
