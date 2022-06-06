package com.kwj.oauth.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class OAuthException extends RuntimeException {

    private final HttpStatus httpStatus;

    public OAuthException(String message) {
        super(message);
        this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public OAuthException(Throwable cause) {
        super(cause);
        this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public OAuthException(String message, Throwable cause) {
        super(message, cause);
        this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public OAuthException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public OAuthException(Throwable cause, HttpStatus httpStatus) {
        super(cause);
        this.httpStatus = httpStatus;
    }

    public OAuthException(String message, Throwable cause, HttpStatus httpStatus) {
        super(message, cause);
        this.httpStatus = httpStatus;
    }

    public OAuthException(String message, int errorCode) {
        super(message);
        this.httpStatus = HttpStatus.valueOf(errorCode);
    }

}
