package com.kwj.oauth.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.net.BindException;
import java.nio.file.AccessDeniedException;
import java.util.Optional;

@Slf4j
@RestControllerAdvice
public class HttpExceptionHandler {

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<OAuthError> handle(Throwable cause) {
        OAuthException exception = new OAuthException(cause.getMessage(), cause);
        return handleOAuthException(exception);
    }

    @ExceptionHandler(HttpStatusCodeException.class)
    public ResponseEntity<OAuthError> handle(HttpStatusCodeException cause) {
        OAuthException exception = new OAuthException(cause.getMessage(), cause, cause.getStatusCode());
        return handleOAuthException(exception);
    }

    @ExceptionHandler({
            ServletRequestBindingException.class,
            BindException.class,
            TypeMismatchException.class,
            IllegalArgumentException.class
    })
    public ResponseEntity<OAuthError> handleBadRequest(Exception cause) {
        OAuthException exception = new OAuthException(cause.getMessage(), cause, HttpStatus.BAD_REQUEST);
        return handleOAuthException(exception);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<OAuthError> handleForbidden(Exception cause) {
        OAuthException exception = new OAuthException(cause.getMessage(), cause, HttpStatus.FORBIDDEN);
        return handleOAuthException(exception);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<OAuthError> handleNotFound(Exception cause) {
        OAuthException exception = new OAuthException(cause.getMessage(), cause, HttpStatus.NOT_FOUND);
        return handleOAuthException(exception);
    }

    @ExceptionHandler(HttpMediaTypeException.class)
    public ResponseEntity<OAuthError> handleUnsupportedMediaType(Exception cause) {
        OAuthException exception = new OAuthException(cause.getMessage(), cause, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        return handleOAuthException(exception);
    }

    @ExceptionHandler(OAuthException.class)
    public ResponseEntity<OAuthError> handleOAuthException(OAuthException exception) {
        OAuthError error = OAuthError.builder()
                .code(exception.getHttpStatus().value())
                .error(exception.getHttpStatus().getReasonPhrase())
                .message(exception.getMessage())
                .build();

        try {
            HttpStatus httpStatus = exception.getHttpStatus();

            if (httpStatus.is4xxClientError()) {
                if (httpStatus != HttpStatus.NOT_FOUND) {
                    String message = Optional.ofNullable(exception.getCause())
                            .orElse(exception)
                            .getMessage();

                    log.warn("status code: {}, message: {}", httpStatus, message);
                }
            } else {
                log.error("status code: {}, message: {}", httpStatus, exception.getMessage(), exception);
            }

            return ResponseEntity.status(httpStatus).body(error);
        } catch (Exception e) {
            log.error("status code: {}", exception.getHttpStatus(), e);

            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error);
        }
    }

}
