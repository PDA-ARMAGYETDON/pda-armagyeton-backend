package com.example.stock_system.pre_trade.exception;

import com.example.common.exception.GlobalException;

public class PreTradeException extends GlobalException {
    public PreTradeException(PreTradeErrorCode errorCode) {
        super(errorCode.getMessage(), null, errorCode.getStatus(), errorCode.getDivisionCode());
    }
}
