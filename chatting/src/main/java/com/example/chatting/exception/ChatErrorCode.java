package com.example.chatting.exception;

import lombok.Getter;

@Getter
public enum ChatErrorCode {
    ROOM_NOT_FOUND(404, "AG401", "채팅방이 존재하지 않습니다."),
    ;
    private final int status;
    private final String divisionCode;
    private final String message;

    ChatErrorCode(int status, String divisionCode, String message) {
        this.status = status;
        this.divisionCode = divisionCode;
        this.message = message;
    }
}
