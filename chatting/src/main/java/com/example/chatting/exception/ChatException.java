package com.example.chatting.exception;

import com.example.common.exception.ErrorCode;
import com.example.common.exception.GlobalException;

public class ChatException extends GlobalException {
    public ChatException(ChatErrorCode errorCode) {
        super(errorCode.getMessage(), null, errorCode.getStatus(), errorCode.getDivisionCode());
    }

    public ChatException(ErrorCode errorCode) {
        super(errorCode.getMessage(), null, errorCode.getStatus(), errorCode.getDivisionCode());
    }
}
