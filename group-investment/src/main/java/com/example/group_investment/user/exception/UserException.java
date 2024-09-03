package com.example.group_investment.user.exception;

import com.example.common.exception.GlobalException;

public class FcmException extends GlobalException {
    public UserException(UserErrorCode errorCode) {
        super(errorCode.getMessage(), null, errorCode.getStatus(), errorCode.getDivisionCode());
    }
}
