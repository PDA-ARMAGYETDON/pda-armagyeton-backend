package com.example.stock_system.pre_trade.exception;

import lombok.Getter;

@Getter
public enum PreTradeErrorCode {
    PRE_TRADE_NOT_FOUND(404, "AG401", "예약 주문이 존재하지 않습니다."),
    ;

    private final int status;
    private final String divisionCode;
    private final String message;

    PreTradeErrorCode(int status, String divisionCode, String message) {
        this.status = status;
        this.divisionCode = divisionCode;
        this.message = message;
    }
}
