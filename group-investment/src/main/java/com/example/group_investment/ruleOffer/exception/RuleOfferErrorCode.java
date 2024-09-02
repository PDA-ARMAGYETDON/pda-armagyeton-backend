package com.example.group_investment.ruleOffer.exception;

import lombok.Getter;

@Getter
public enum RuleOfferErrorCode {
    RULE_OFFER_NOT_FOUND(404, "AG401", "규칙 제안이 존재하지 않습니다."),
    ;

    private final int status;
    private final String divisionCode;
    private final String message;

    RuleOfferErrorCode(int status, String divisionCode, String message) {
        this.status = status;
        this.divisionCode = divisionCode;
        this.message = message;
    }
}
