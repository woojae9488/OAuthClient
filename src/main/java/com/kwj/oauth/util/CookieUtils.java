package com.kwj.oauth.util;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Optional;

@UtilityClass
public class CookieUtils {

    public Optional<Cookie> getCookie(HttpServletRequest request, String key) {
        return Optional.ofNullable(request.getCookies())
                .stream()
                .flatMap(Arrays::stream)
                .filter(cookie -> StringUtils.equals(cookie.getName(), key))
                .findFirst();
    }

    public void setCookie(HttpServletResponse response, String key, String value, int maxAge) {
        Cookie cookie = new Cookie(key, value);
        cookie.setPath("/");
        cookie.setHttpOnly(true); // Frontend can not receive token by cookie
        cookie.setMaxAge(maxAge);

        response.addCookie(cookie);
    }

    public void deleteCookie(HttpServletRequest request, HttpServletResponse response, String key) {
        Optional<Cookie> cookieOptional = getCookie(request, key);

        cookieOptional.ifPresent(cookie -> {
            cookie.setValue("");
            cookie.setPath("/");
            cookie.setMaxAge(0);

            response.addCookie(cookie);
        });
    }

}
