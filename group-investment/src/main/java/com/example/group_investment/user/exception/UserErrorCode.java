package com.example.group_investment.user.exception;

import lombok.Getter;

@Getter
public enum UserErrorCode {
    USER_NOT_FOUND(404, "AG401", "사용자가 존재하지 않습니다."),
    ;

    private final int status;
    private final String divisionCode;
    private final String message;

    UserErrorCode(int status, String divisionCode, String message) {
        this.status = status;
        this.divisionCode = divisionCode;
        this.message = message;
    }
}
