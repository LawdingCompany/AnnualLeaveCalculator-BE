package com.lawding.global.exception;

public class ServerException extends ApplicationException {

    public ServerException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ServerException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public ServerException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    public ServerException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}
