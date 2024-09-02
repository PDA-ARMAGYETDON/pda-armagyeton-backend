package com.example.common.exception;

import lombok.Getter;

@Getter
public class GlobalException extends RuntimeException {

    private final int code;
    private final String divisionCode;

    public GlobalException(String message, Throwable cause, int code, String divisionCode) {
        super(message, cause);
        this.code = code;
        this.divisionCode = divisionCode;
    }

    public GlobalException(String message, int code, String divisionCode) {
        super(message, null);
        this.code = code;
        this.divisionCode = divisionCode;
    }
}
