package com.example.alarm.firebase.exception;

import com.example.common.exception.ErrorCode;
import com.example.common.exception.GlobalException;

public class AlarmException extends GlobalException {

    public AlarmException(AlarmErrorCode errorCode) {
        super(errorCode.getMessage(), null, errorCode.getStatus(), errorCode.getDivisionCode());
    }

    public AlarmException(ErrorCode errorCode) {
        super(errorCode.getMessage(), null, errorCode.getStatus(), errorCode.getDivisionCode());
    }
}
