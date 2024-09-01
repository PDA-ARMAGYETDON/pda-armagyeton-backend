package com.example.armagyetdon.rule.exception;

import lombok.Getter;

@Getter
public enum RuleErrorCode {
    RULE_NOT_FOUND(404, "AG401", "규칙이 존재하지 않습니다."),
    RULE_SAVE_FAILED(400, "AG001", "규칙 생성에 실패했습니다."),
    ;

    private final int status;
    private final String divisionCode;
    private final String message;

    RuleErrorCode(int status, String divisionCode, String message) {
        this.status = status;
        this.divisionCode = divisionCode;
        this.message = message;
    }
}
