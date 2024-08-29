package com.example.stock_system.stocks.exception;

import lombok.Getter;

@Getter
public enum StocksErrorCode {
    STOCKS_NOT_FOUND(404, "AG401", "종목이 존재하지 않습니다."),
    ;

    private final int status;
    private final String divisionCode;
    private final String message;

    StocksErrorCode(int status, String divisionCode, String message) {
        this.status = status;
        this.divisionCode = divisionCode;
        this.message = message;
    }
}
