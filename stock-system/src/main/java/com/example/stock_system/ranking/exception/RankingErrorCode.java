package com.example.stock_system.ranking.exception;

import lombok.Getter;

@Getter
public enum RankingErrorCode {
    RANKING_NOT_FOUNT(404, "AG401", "랭킹이 존재하지 않습니다."),
    ;

    private final int status;
    private final String divisionCode;
    private final String message;

    RankingErrorCode(int status, String divisionCode, String message) {
        this.status = status;
        this.divisionCode = divisionCode;
        this.message = message;
    }
}
