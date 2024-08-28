package com.example.armagyetdon.rule.exception;

import com.example.common.exception.GlobalException;

public class RuleException extends GlobalException {
    public RuleException(RuleErrorCode errorCode) {
        super(errorCode.getMessage(), null, errorCode.getStatus(), errorCode.getDivisionCode());
    }
}
