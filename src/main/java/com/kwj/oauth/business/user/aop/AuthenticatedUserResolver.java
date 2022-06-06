package com.kwj.oauth.business.user.aop;

import com.kwj.oauth.business.security.application.SecurityContextHelper;
import com.kwj.oauth.business.security.model.OAuthUserPrincipal;
import com.kwj.oauth.business.user.domain.SocialUser;
import com.kwj.oauth.exception.OAuthException;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Optional;

@Component
public class AuthenticatedUserResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(AuthenticatedUser.class)
                && parameter.getParameterType() == SocialUser.class;
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) {
        OAuthUserPrincipal userPrincipal = SecurityContextHelper.getOAuthUserPrincipal();

        return Optional.of(userPrincipal)
                .map(OAuthUserPrincipal::getSocialUser)
                .orElseThrow(() -> new OAuthException("Failed to resolve authenticated user", HttpStatus.UNAUTHORIZED));
    }

}
