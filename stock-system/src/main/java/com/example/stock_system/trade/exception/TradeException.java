package com.example.stock_system.trade.exception;

import com.example.common.exception.GlobalException;

public class TradeException extends GlobalException {
    public TradeException(TradeErrorCode errorCode) {
        super(errorCode.getMessage(), null, errorCode.getStatus(), errorCode.getDivisionCode());
    }
}
