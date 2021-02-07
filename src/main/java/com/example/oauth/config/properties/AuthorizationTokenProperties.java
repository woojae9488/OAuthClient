package com.example.oauth.config.properties;

import com.example.oauth.model.token.TokenType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter(value = AccessLevel.PRIVATE)
@Setter
public class AuthorizationTokenProperties {
    private final String accessTokenCookieKey = "access-token";
    private final String refreshTokenCookieKey = "refresh-token";

    @Value("${security.token.access.secret}")
    private String accessTokenSecret;
    @Value("${security.token.access.expirationMsec}")
    private Long accessTokenExpirationMsec;

    @Value("${security.token.refresh.secret}")
    private String refreshTokenSecret;
    @Value("${security.token.refresh.expirationMsec}")
    private Long refreshTokenExpirationMsec;

    public String getTokenSecret(TokenType tokenType) {
        switch (tokenType) {
            case ACCESS_TOKEN:
                return this.getAccessTokenSecret();
            case REFRESH_TOKEN:
                return this.getRefreshTokenSecret();
            default:
                return "";
        }
    }

    public Long getTokenExpirationMsec(TokenType tokenType) {
        switch (tokenType) {
            case ACCESS_TOKEN:
                return this.getAccessTokenExpirationMsec();
            case REFRESH_TOKEN:
                return this.getRefreshTokenExpirationMsec();
            default:
                return 0L;
        }
    }

    public String getTokenCookieKey(TokenType tokenType) {
        switch (tokenType) {
            case ACCESS_TOKEN:
                return this.getAccessTokenCookieKey();
            case REFRESH_TOKEN:
                return this.getRefreshTokenCookieKey();
            default:
                return "";
        }
    }

    public int getTokenCookieMaxAge(TokenType tokenType) {
        switch (tokenType) {
            case ACCESS_TOKEN:
                return this.getAccessTokenCookieMaxAge();
            case REFRESH_TOKEN:
                return this.getRefreshTokenCookieMaxAge();
            default:
                return 0;
        }
    }

    private int getAccessTokenCookieMaxAge() {
        return Long.valueOf(accessTokenExpirationMsec / 1000).intValue();
    }

    private int getRefreshTokenCookieMaxAge() {
        return Long.valueOf(refreshTokenExpirationMsec / 1000).intValue();
    }
}
