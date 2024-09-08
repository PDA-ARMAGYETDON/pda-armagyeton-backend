package com.example.group_investment.user.exception;

import lombok.Getter;

@Getter
public enum UserErrorCode {
    USER_NOT_FOUND(404, "AG401", "사용자가 존재하지 않습니다."),
    USER_ALREADY_EXISTS(409, "AG901", "이미 존재하는 아이디입니다."),
    EMAIL_ALREADY_EXISTS(409, "AG901", "이미 존재하는 이메일입니다."),
    FORBIDDEN_ERROR(403, "AG403", "접근 권한이 없습니다."),
    USER_NOT_ACTIVE(403, "AG404", "탈퇴한 사용자입니다.");

    private final int status;
    private final String divisionCode;
    private final String message;

    UserErrorCode(int status, String divisionCode, String message) {
        this.status = status;
        this.divisionCode = divisionCode;
        this.message = message;
    }
}
