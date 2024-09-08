package com.example.stock_system.ranking.exception;

import com.example.common.exception.GlobalException;

public class RankingException extends GlobalException {
    public RankingException(RankingErrorCode errorCode) {
        super(errorCode.getMessage(), null, errorCode.getStatus(), errorCode.getDivisionCode());
    }
}