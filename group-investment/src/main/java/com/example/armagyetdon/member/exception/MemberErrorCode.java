package com.example.armagyetdon.member.exception;

import lombok.Getter;

@Getter
public enum MemberErrorCode {
    MEMBER_NOT_FOUND(404, "AG401", "멤버가 존재하지 않습니다."),
    TRADE_OFFER_SAVE_FAILED(400, "AG001", "매매 제안에 실패했습니다."),
    ;

    private final int status;
    private final String divisionCode;
    private final String message;

    MemberErrorCode(int status, String divisionCode, String message) {
        this.status = status;
        this.divisionCode = divisionCode;
        this.message = message;
    }
}
