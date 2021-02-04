package com.example.oauth.config.token;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class AuthenticationTokenProperties {
    public static final String HEADER_STRING = "Authorization";
    public static final String ACCESS_TOKEN_COOKIE_STRING = "access-token";
    public static final int ACCESS_TOKEN_COOKIE_MAX_AGE = 60 * 60;
    public static final String REFRESH_TOKEN_COOKIE_STRING = "refresh-token";
    public static final int REFRESH_TOKEN_COOKIE_MAX_AGE = 60 * 60 * 24 * 7;
    // TODO : must change cookie max age

    @Value("${security.token.secret}")
    private String tokenSecret;
    @Value("${security.token.expirationMsec}")
    private Long tokenExpirationMsec;

}
