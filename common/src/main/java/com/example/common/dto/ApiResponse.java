package com.example.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiResponse<D> {
    protected final int code;
    protected final boolean isSuccess;
    protected final String message;
    protected final D data;
}
