package com.example.alarm.firebase.exception;

import lombok.Getter;

@Getter
public enum AlarmErrorCode {
    ACCOUNT_NOT_FOUND(404, "AG401", "계좌가 존재하지 않습니다."),
    PRIVATE_ACCOUNT_NOT_FOUND(404, "AG402", "개인계좌가 존재하지 않습니다."),
    TEAM_ACCOUNT_NOT_FOUND(404, "AG403", "모임계좌, 혹은 모임이 존재하지 않습니다."),
    FCM_TOKEN_NOT_FOUND(404, "AG404", "해당 유저의 FCM 토큰이 존재하지 않습니다."),
    ;

    private final int status;
    private final String divisionCode;
    private final String message;

    AlarmErrorCode(int status, String divisionCode, String message) {
        this.status = status;
        this.divisionCode = divisionCode;
        this.message = message;
    }
}
