package com.example.group_investment.auth.exception;

import lombok.Getter;

@Getter
public enum AuthErrorCode {
    UNAUTHORIZATION(404, "AG401", "Authorization 헤더가 존재하지 않습니다."),
    INVALID_JWT_TOKEN(401, "AG402", "JWT 토큰이 유효하지 않습니다."),
    EXPIRED_JWT_TOKEN(401, "AG403", "JWT 토큰이 만료되었습니다."),
    LOGIN_FAILED(404, "AG404", "로그인에 실패하였습니다.");

    private final int status;
    private final String divisionCode;
    private final String message;

    AuthErrorCode(int status, String divisionCode, String message) {
        this.status = status;
        this.divisionCode = divisionCode;
        this.message = message;
    }
}
