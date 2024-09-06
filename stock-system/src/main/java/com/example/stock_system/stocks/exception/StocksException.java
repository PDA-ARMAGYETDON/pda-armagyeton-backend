package com.example.stock_system.stocks.exception;

import com.example.common.exception.ErrorCode;
import com.example.common.exception.GlobalException;

public class StocksException extends GlobalException {

    public StocksException(StocksErrorCode errorCode) {
        super(errorCode.getMessage(), null, errorCode.getStatus(), errorCode.getDivisionCode());
    }

    public StocksException(ErrorCode errorCode) {
        super(errorCode.getMessage(), null, errorCode.getStatus(), errorCode.getDivisionCode());
    }
}