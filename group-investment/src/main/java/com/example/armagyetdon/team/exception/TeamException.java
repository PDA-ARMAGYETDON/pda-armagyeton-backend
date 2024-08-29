package com.example.armagyetdon.team.exception;

import com.example.common.exception.GlobalException;

public class TeamException extends GlobalException {
    public TeamException(TeamErrorCode errorCode) {
        super(errorCode.getMessage(), null, errorCode.getStatus(), errorCode.getDivisionCode());
    }
}
