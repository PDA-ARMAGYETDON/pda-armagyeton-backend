package com.example.stock_system.transferHistory.exception;

import com.example.common.exception.GlobalException;

public class TransferHistoryException extends GlobalException {
    public TransferHistoryException(TransferHistoryErrorCode errorCode) {
        super(errorCode.getMessage(), null, errorCode.getStatus(), errorCode.getDivisionCode());
    }
}
