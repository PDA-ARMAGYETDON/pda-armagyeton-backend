package com.example.common.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    /**
     * ******************************* Global Error CodeList ***************************************
     * HTTP Status Code
     * 400 : Bad Request
     * 401 : Unauthorized
     * 403 : Forbidden
     * 404 : Not Found
     * 500 : Internal Server Error
     * *********************************************************************************************
     */
    BAD_REQUEST_ERROR(400, "AG001", "잘못된 서버 요청입니다."),

    // @RequestBody 데이터 미 존재
    REQUEST_BODY_MISSING_ERROR(400, "AG002", "request body가 없습니다."),

    // 유효하지 않은 타입
    INVALID_TYPE_VALUE(400, "AG003", "값이 유효하지 않습니다."),

    // Request Parameter 로 데이터가 전달되지 않을 경우
    MISSING_REQUEST_PARAMETER_ERROR(400, "AG004", "Request Parameter가 없습니다."),

    // 입력/출력 값이 유효하지 않음
    IO_ERROR(400, "AG005", "입출력 값이 유효하지 않습니다."),

    // com.google.gson JSON 파싱 실패
    JSON_PARSE_ERROR(400, "AG006", "파싱에 실패했습니다."),

    // com.fasterxml.jackson.core Processing Error
    JACKSON_PROCESS_ERROR(400, "AG007", "Jackson 파싱에 실패했습니다."),

    // 권한이 없음
    FORBIDDEN_ERROR(403, "AG301", "접근할 수 있는 권한이 없습니다."),

    // 서버로 요청한 리소스가 존재하지 않음
    NOT_FOUND_ERROR(404, "AG401", "데이터가 존재하지 않습니다."),

    // NULL Point Exception 발생
    NULL_POINT_ERROR(404, "AG402", "요청값에 Null이 존재합니다."),

    // @RequestBody 및 @RequestParam, @PathVariable 값이 유효하지 않음
    NOT_VALID_BODY_ERROR(404, "AG403", "RequestBody 요청이 유효하지 않습니다."),
    NOT_VALID_PARAM_ERROR(404, "AG404", "RequestParam 요청이 유효하지 않습니다."),
    NOT_VALID_PATH_ERROR(404, "AG405", "PathVariable 요청이 유효하지 않습니다."),

    // Header 값이 유효하지 않음
    NOT_VALID_HEADER_ERROR(404, "AG406", "필요한 Header 데이터가 존재하지 않습니다."),

    // 서버가 처리 할 방법을 모르는 경우 발생
    INTERNAL_SERVER_ERROR(500, "AG900", "내부 서버 에러가 발생했습니다."),

    ;

    /**
     * ******************************* Error Code Constructor ***************************************
     */
    // 에러 코드의 '코드 상태'을 반환한다.
    private final int status;

    // 에러 코드의 '코드간 구분 값'을 반환한다.
    private final String divisionCode;

    // 에러 코드의 '코드 메시지'을 반환한다.
    private final String message;


    ErrorCode(final int status, final String divisionCode, final String message) {
        this.status = status;
        this.divisionCode = divisionCode;
        this.message = message;
    }

}
