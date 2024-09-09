package com.example.group_investment.ruleOffer.exception;

import com.example.common.exception.ErrorCode;
import com.example.common.exception.GlobalException;

public class RuleOfferException extends GlobalException {
    public RuleOfferException(RuleOfferErrorCode errorCode) {
        super(errorCode.getMessage(), null, errorCode.getStatus(), errorCode.getDivisionCode());
    }

    public RuleOfferException(ErrorCode errorCode) {
        super(errorCode.getMessage(), null, errorCode.getStatus(), errorCode.getDivisionCode());
    }
}
