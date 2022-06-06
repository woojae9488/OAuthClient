package com.kwj.oauth.business.token.application;

import com.kwj.oauth.business.token.model.TokenType;
import com.kwj.oauth.config.properties.AuthorizationTokenProperties;
import com.kwj.oauth.util.CookieUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
@RequiredArgsConstructor
public class TokenCookieService {

    private final AuthorizationTokenProperties tokenProperties;

    public String getTokenFromRequest(TokenType tokenType, HttpServletRequest request) {
        return CookieUtils.getCookie(request, tokenProperties.getTokenCookieKey(tokenType))
                .map(Cookie::getValue)
                .filter(StringUtils::isNotBlank)
                .orElse(null);
    }

    public void saveTokenToCookie(HttpServletResponse response, TokenType tokenType, String token) {
        String cookieKey = tokenProperties.getTokenCookieKey(tokenType);
        int cookieMaxAge = tokenProperties.getTokenCookieMaxAge(tokenType);

        CookieUtils.setCookie(response, cookieKey, token, cookieMaxAge);
    }

}
