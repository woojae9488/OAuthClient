package com.kwj.oauth.business.security.application;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

public class ServletContextHelper {

    public static <T> Optional<T> getAttributeWithRemove(HttpServletRequest request, String key, Class<T> clazz) {
        Optional<T> value = getAttribute(request, key, clazz);
        removeAttribute(request, key);
        return value;
    }

    public static <T> Optional<T> getAttribute(HttpServletRequest request, String key, Class<T> clazz) {
        return Optional.of(request)
                .map(HttpServletRequest::getServletContext)
                .map(context -> context.getAttribute(key))
                .filter(clazz::isInstance)
                .map(clazz::cast);
    }

    public static void removeAttribute(HttpServletRequest request, String key) {
        Optional.of(request)
                .map(HttpServletRequest::getServletContext)
                .ifPresent(context -> context.removeAttribute(key));
    }

    public static void setAttribute(HttpServletRequest request, String key, Object value) {
        Optional.of(request)
                .map(HttpServletRequest::getServletContext)
                .ifPresent(context -> context.setAttribute(key, value));
    }

}
