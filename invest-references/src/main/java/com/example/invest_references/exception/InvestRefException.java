package com.example.invest_references.exception;

import com.example.common.exception.ErrorCode;
import com.example.common.exception.GlobalException;

public class InvestRefException extends GlobalException {
    public InvestRefException(InvestRefErrorCode errorCode) {
        super(errorCode.getMessage(), null, errorCode.getStatus(), errorCode.getDivisionCode());
    }

    public InvestRefException(ErrorCode errorCode) {
        super(errorCode.getMessage(), null, errorCode.getStatus(), errorCode.getDivisionCode());
    }
}
