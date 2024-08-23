package com.example.common.exception;

import lombok.*;
import org.springframework.http.ResponseEntity;

@Data
@Builder
public class ErrorResponse {

    private int status;                 // 에러 상태 코드
    private String divisionCode;        // 에러 구분 코드
    private String message;           // 에러 메시지

    public static ResponseEntity<ErrorResponse> toResponseEntity(ErrorCode e){
        return ResponseEntity
                .status(e.getStatus())
                .body(ErrorResponse.builder()
                        .status(e.getStatus())
                        .divisionCode(e.getDivisionCode())
                        .message(e.getMessage())
                        .build());
    }
}
