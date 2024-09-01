package com.example.armagyetdon.member.exception;

import com.example.common.exception.GlobalException;

public class MemberException extends GlobalException {
    public MemberException(MemberErrorCode errorCode) {
        super(errorCode.getMessage(), null, errorCode.getStatus(), errorCode.getDivisionCode());
    }
}
