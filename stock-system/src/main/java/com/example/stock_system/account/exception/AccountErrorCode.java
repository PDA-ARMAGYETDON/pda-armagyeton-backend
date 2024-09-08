package com.example.stock_system.account.exception;

import lombok.Getter;

@Getter
public enum AccountErrorCode {
    ACCOUNT_NOT_FOUND(404, "AG401", "계좌가 존재하지 않습니다."),
    PRIVATE_ACCOUNT_NOT_FOUND(404, "AG402", "개인 계좌가 존재하지 않습니다."),
    TEAM_ACCOUNT_NOT_FOUND(404, "AG403", "모임계좌, 혹은 모임이 존재하지 않습니다."),
    NOT_ENOUGH_DEPOSIT(400, "AG001", "예수금이 부족합니다."),
    NO_HOLDINGS(404, "AG404", "아무 종목도 보유하고 있지 않습니다."),
    ;

    private final int status;
    private final String divisionCode;
    private final String message;

    AccountErrorCode(int status, String divisionCode, String message) {
        this.status = status;
        this.divisionCode = divisionCode;
        this.message = message;
    }
}
