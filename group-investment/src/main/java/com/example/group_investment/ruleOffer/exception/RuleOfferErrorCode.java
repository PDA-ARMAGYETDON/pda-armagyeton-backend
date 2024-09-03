package com.example.group_investment.ruleOffer.exception;

import lombok.Getter;

@Getter
public enum RuleOfferErrorCode {
    RULE_OFFER_NOT_FOUND(404, "AG401", "규칙 제안이 존재하지 않습니다."),
    RULE_OFFER_SAVE_FAILED(400, "AG001", "규칙 제안 저장에 실패했습니다."),
    RULE_OFFER_UPDATE_FAILED(400, "AG001", "규칙 제안 Update에 실패했습니다.")
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
