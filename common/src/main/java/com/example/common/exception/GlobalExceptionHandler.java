package com.example.common.exception;

import com.example.common.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(GlobalException.class)
    public ApiResponse handleGlobalException(GlobalException exception) {
        return new ApiResponse(exception.getCode(), false, exception.getMessage(), null);
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse handleAllException(Exception exception) {
        return new ApiResponse(500, false, "내부 서버 에러가 발생했습니다.", null);
    }
}
