package com.example.group_investment.auth.exception;

import com.example.common.exception.GlobalException;

public class AuthoException extends GlobalException {
    public AuthoException(AuthoErrorCode errorCode) {
        super(errorCode.getMessage(), null, errorCode.getStatus(), errorCode.getDivisionCode());
    }
}
