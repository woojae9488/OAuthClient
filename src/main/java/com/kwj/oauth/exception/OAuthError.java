package com.kwj.oauth.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class OAuthError {

    private final int code;

    private final String error;

    private final String message;

}
