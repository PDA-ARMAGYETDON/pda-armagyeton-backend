package com.example.group_investment.auth.exception;

import lombok.Getter;

@Getter
public enum AuthoErrorCode {
    UNAUTHORIZATION(404, "AG401", "Authorization 헤더가 존재하지 않습니다."),
    INVALID_JWT_TOKEN(401, "AG402", "JWT 토큰이 유효하지 않습니다."),
    EXPIRED_JWT_TOKEN(401, "AG403", "JWT 토큰이 만료되었습니다."),
    LOGIN_FAILED(404, "AG404", "로그인에 실패하였습니다."),
    NOT_TEAM_MEMBER(400, "AG406", "팀에 속해있지 않습니다."),
    IO_EXCEPTION(500, "AG405", "IO 예외가 발생하였습니다. 다시 시도해주세요.")
    ;

    private final int status;
    private final String divisionCode;
    private final String message;

    AuthoErrorCode(int status, String divisionCode, String message) {
        this.status = status;
        this.divisionCode = divisionCode;
        this.message = message;
    }
}
