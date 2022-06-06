package com.kwj.oauth.business.security.filter;

import com.kwj.oauth.business.security.application.SecurityContextHelper;
import com.kwj.oauth.business.security.model.OAuthUserPrincipal;
import com.kwj.oauth.business.token.application.AuthorizationTokenService;
import com.kwj.oauth.business.token.application.TokenCookieService;
import com.kwj.oauth.business.token.model.TokenType;
import com.kwj.oauth.business.user.domain.SocialUser;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final AuthorizationTokenService tokenService;
    private final TokenCookieService tokenCookieService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String accessToken = tokenCookieService.getTokenFromRequest(TokenType.ACCESS_TOKEN, request);
        String refreshToken = tokenCookieService.getTokenFromRequest(TokenType.REFRESH_TOKEN, request);

        if (ObjectUtils.anyNull(accessToken, refreshToken)) {
            filterChain.doFilter(request, response);
            return;
        }

        Authentication authentication = attemptAuthentication(request, accessToken);

        if (Objects.nonNull(authentication)) {
            SecurityContextHelper.setAuthentication(authentication);
        } else {
            String refreshedAccessToken = tokenService.refreshAccessToken(accessToken, refreshToken);
            tokenCookieService.saveTokenToCookie(response, TokenType.ACCESS_TOKEN, refreshedAccessToken);

            Authentication refreshedAuthentication = attemptAuthentication(request, refreshedAccessToken);
            SecurityContextHelper.setAuthentication(refreshedAuthentication);
        }

        filterChain.doFilter(request, response);
    }


    private Authentication attemptAuthentication(HttpServletRequest request, String accessToken) {
        if (tokenService.isValidToken(TokenType.ACCESS_TOKEN, accessToken)) {
            SocialUser pseudoSocialUser = tokenService.getPseudoSocialUserFromToken(TokenType.ACCESS_TOKEN, accessToken);
            OAuthUserPrincipal userPrincipal = new OAuthUserPrincipal(pseudoSocialUser);

            return SecurityContextHelper.generateAuthentication(request, userPrincipal);
        }

        return null;
    }

}
