package com.example.group_investment.rule.exception;

import com.example.common.exception.ErrorCode;
import com.example.common.exception.GlobalException;

public class RuleException extends GlobalException {
    public RuleException(RuleErrorCode errorCode) {
        super(errorCode.getMessage(), null, errorCode.getStatus(), errorCode.getDivisionCode());
    }

    public RuleException(ErrorCode errorCode) {
        super(errorCode.getMessage(), null, errorCode.getStatus(), errorCode.getDivisionCode());
    }
}
