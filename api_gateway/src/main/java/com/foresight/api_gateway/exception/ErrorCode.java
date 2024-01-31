package com.foresight.api_gateway.exception;


public enum ErrorCode {
    USER_NOT_FOUND_EXCEPTION(1),
    EMAIL_ALREADY_EXISTS(2),
    INVALID_AUTHENTICATION_TOKEN(3),
    INVALID_REQUEST_BODY(4),
    AUTHENTICATION_EXCEPTION(5),
    DEACTIVATED_ACCOUNT(6),
    MISSING_AUTHORIZATION_HEADER(7),
    UNKNOWN_SERVER_ERROR(100);

    private final int code;
    ErrorCode(int code)
    {
        this.code = code;

    }

    public int getCode()
    {
        return code;
    }



}

