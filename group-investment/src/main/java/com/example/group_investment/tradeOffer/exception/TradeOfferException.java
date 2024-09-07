package com.example.group_investment.tradeOffer.exception;

import com.example.common.exception.ErrorCode;
import com.example.common.exception.GlobalException;

public class TradeOfferException extends GlobalException {
    public TradeOfferException(TradeOfferErrorCode errorCode) {
        super(errorCode.getMessage(), null, errorCode.getStatus(), errorCode.getDivisionCode());
    }

    public TradeOfferException(ErrorCode errorCode) {
        super(errorCode.getMessage(), null, errorCode.getStatus(), errorCode.getDivisionCode());
    }
}
