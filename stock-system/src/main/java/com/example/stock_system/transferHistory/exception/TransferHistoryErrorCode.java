package com.example.stock_system.transferHistory.exception;

import lombok.Getter;

@Getter
public enum TransferHistoryErrorCode {
    TRANSFER_HISTORY_NOT_FOUND(404, "AG401", "내역이 존재하지 않습니다."),
    ;

    private final int status;
    private final String divisionCode;
    private final String message;

    TransferHistoryErrorCode(int status, String divisionCode, String message) {
        this.status = status;
        this.divisionCode = divisionCode;
        this.message = message;
    }
}
