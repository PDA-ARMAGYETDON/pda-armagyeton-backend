package com.example.armagyetdon.account.exception;

import lombok.Getter;

@Getter
public enum AccountErrorCode {
    ACCOUNT_NOT_FOUND(404, "AG401", "계좌가 존재하지 않습니다."),
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
