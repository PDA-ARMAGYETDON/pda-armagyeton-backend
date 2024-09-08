package com.example.invest_references.exception;

import lombok.Getter;

@Getter
public enum InvestRefErrorCode {
    FAILED_TO_GET_NEWS(400, "AG001", "네이버 증권 뉴스를 가져오는 데 실패했습니다."),
    FAILED_TO_GET_MARKET_INDEX(400, "AG002", "코스피/코스닥 지수를 가져오는 데 실패했습니다."),
    FAILED_TO_GET_RANKING(400,"AG003","현재 뜨는 테마를 가져오는데에 실패했습니다."),

    FAILED_TO_GET_VOLUME(400,"AG004","거래량 상위 종목을 가져오는데에 실패했습니다."),

    FAILED_TO_GET_EARNING(400,"AG005","수익률 상위 종목을 가져오는데에 실패했습니다.");


    private final int status;
    private final String divisionCode;
    private final String message;

    InvestRefErrorCode(int status, String divisionCode, String message) {
        this.status = status;
        this.divisionCode = divisionCode;
        this.message = message;
    }
}
