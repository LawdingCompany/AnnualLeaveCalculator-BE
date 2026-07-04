package com.lawding.global.exception;

public class ClientException extends ApplicationException {

    public ClientException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ClientException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
