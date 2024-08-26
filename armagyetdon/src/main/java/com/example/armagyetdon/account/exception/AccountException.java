package com.example.armagyetdon.account.exception;

import com.example.common.exception.GlobalException;

public class AccountException extends GlobalException {
    public AccountException(AccountErrorCode errorCode) {
        super(errorCode.getMessage(), null, errorCode.getStatus(), errorCode.getDivisionCode());
    }
}
