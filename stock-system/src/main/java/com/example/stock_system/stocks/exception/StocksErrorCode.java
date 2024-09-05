package com.example.stock_system.stocks.exception;

import lombok.Getter;

@Getter
public enum StocksErrorCode {
    STOCKS_NOT_FOUND(404, "AG401", "종목이 존재하지 않습니다."),
    API_BAD_REQUEST(400, "AG001", "API 호출에 이상이 있습니다."),
    API_BAD_RESPONSE(404, "AG402", "API 응답이 존재하지 않습니다."),
    ACCESS_TOKEN_BAD_REQUEST(400, "AG002", "액세스 토큰 요청에 실패했습니다."),
    STOCKS_PRICE_CONNECTION_ERROR(400, "AG003", "종목 가격 조회에 실패했습니다."),
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
