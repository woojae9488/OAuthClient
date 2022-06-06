package com.kwj.oauth.business.security.application;

import com.kwj.oauth.business.security.model.OAuthUserPrincipal;
import com.kwj.oauth.exception.OAuthException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

public class SecurityContextHelper {

    public static OAuthUserPrincipal getOAuthUserPrincipal() {
        return Optional.of(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getPrincipal)
                .filter(OAuthUserPrincipal.class::isInstance)
                .map(OAuthUserPrincipal.class::cast)
                .orElseThrow(() -> new OAuthException("Failed to get OAuthUserPrincipal"));
    }

    public static void setAuthentication(Authentication authentication) {
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    public static Authentication generateAuthentication(HttpServletRequest request, OAuthUserPrincipal userPrincipal) {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetails(request));

        return authentication;
    }

}
