package com.example.group_investment.tradeOffer.exception;

import lombok.Getter;

@Getter
public enum TradeOfferErrorCode {
    STOCKS_SERVER_BAD_REQUEST(400, "AG001", "증권 시스템 서버 요청에 실패했습니다."),
    ASSET_NOT_ENOUGH(402, "AG201", "자산이 부족합니다."),
    TRADE_OFFER_EXPIRED(403, "AG301", "만료된 제안입니다."),
    TRADE_OFFER_NOT_FOUND(404, "AG401", "매매제안이 존재하지 않습니다."),
    HOLDINGS_NOT_ENOUGH(409, "AG903", "보유 수량이 충분하지 않습니다."),
    ALREADY_VOTED(409, "AG902", "이미 투표한 매매제안입니다."),
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
