package com.example.group_investment.ruleOfferVote.exception;

import com.example.common.exception.GlobalException;

public class RuleOfferVoteException extends GlobalException {
    public RuleOfferVoteException(RuleOfferVoteErrorCode errorCode) {
        super(errorCode.getMessage(), null, errorCode.getStatus(), errorCode.getDivisionCode());
    }
}
