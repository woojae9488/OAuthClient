package com.kwj.oauth.config.properties;

import com.kwj.oauth.business.token.model.TokenType;
import lombok.Setter;
import lombok.ToString;


@Setter
@ToString
public class AuthorizationTokenProperties {

    private static final String accessTokenCookieKey = "access-token";
    private static final String refreshTokenCookieKey = "refresh-token";

    private TokenMeta access;
    private TokenMeta refresh;

    @Setter
    @ToString
    public static class TokenMeta {
        private String secret;
        private Long expirationMillis;
    }

    public String getTokenSecret(TokenType tokenType) {
        switch (tokenType) {
            case ACCESS_TOKEN:
                return this.access.secret;
            case REFRESH_TOKEN:
            default:
                return this.refresh.secret;
        }
    }

    public Long getTokenExpirationMillis(TokenType tokenType) {
        switch (tokenType) {
            case ACCESS_TOKEN:
                return this.access.expirationMillis;
            case REFRESH_TOKEN:
            default:
                return this.refresh.expirationMillis;
        }
    }

    public String getTokenCookieKey(TokenType tokenType) {
        switch (tokenType) {
            case ACCESS_TOKEN:
                return AuthorizationTokenProperties.accessTokenCookieKey;
            case REFRESH_TOKEN:
            default:
                return AuthorizationTokenProperties.refreshTokenCookieKey;
        }
    }

    public int getTokenCookieMaxAge(TokenType tokenType) {
        return Math.toIntExact(getTokenExpirationMillis(tokenType) / 1000);
    }

}
