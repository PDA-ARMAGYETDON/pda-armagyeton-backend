package com.example.group_investment.tradeOffer.exception;

import lombok.Getter;

@Getter
public enum TradeOfferErrorCode {
    TRADE_OFFER_NOT_FOUND(404, "AG401", "매매제안이 존재하지 않습니다."),
    STOCKS_SERVER_BAD_REQUEST(400, "AG001", "증권 시스템 서버 요청에 실패했습니다."),
    ;

    private final int status;
    private final String divisionCode;
    private final String message;

    TradeOfferErrorCode(int status, String divisionCode, String message) {
        this.status = status;
        this.divisionCode = divisionCode;
        this.message = message;
    }
}
