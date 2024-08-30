package com.example.group_investment.team.exception;

import lombok.Getter;

@Getter
public enum TeamErrorCode {
    TEAM_NOT_FOUND(404, "AG401", "모임이 존재하지 않습니다."),
    ;

    private final int status;
    private final String divisionCode;
    private final String message;

    TeamErrorCode(int status, String divisionCode, String message) {
        this.status = status;
        this.divisionCode = divisionCode;
        this.message = message;
    }
}
