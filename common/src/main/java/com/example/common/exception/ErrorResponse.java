package com.example.common.exception;

import lombok.*;
import org.springframework.http.ResponseEntity;

@Data
@Builder
public class ErrorResponse {

    private int status;                 // 에러 상태 코드
    private String divisionCode;        // 에러 구분 코드
    private String message;           // 에러 메시지

    public ErrorResponse(int status, String divisionCode, String message) {
        this.status = status;
        this.divisionCode = divisionCode;
        this.message = message;
    }
}
