package com.example.stock_system.holdings.exception;

import com.example.common.exception.GlobalException;

public class HoldingsException extends GlobalException {
    public HoldingsException(HoldingsErrorCode errorCode) {
        super(errorCode.getMessage(), null, errorCode.getStatus(), errorCode.getDivisionCode());
    }
}
