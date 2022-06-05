package com.kwj.oauth.business.security.filter;

import com.kwj.oauth.business.security.model.OAuthUserPrincipal;
import com.kwj.oauth.business.token.application.AuthorizationTokenService;
import com.kwj.oauth.business.token.model.TokenType;
import com.kwj.oauth.config.properties.AuthorizationTokenProperties;
import com.kwj.oauth.business.user.domain.SocialUser;
import com.kwj.oauth.util.CookieUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.mail.AuthenticationFailedException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final AuthorizationTokenService tokenService;
    private final AuthorizationTokenProperties tokenProperties;

    @SneakyThrows(AuthenticationFailedException.class)
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String accessToken = getTokenFromRequest(TokenType.ACCESS_TOKEN, request);
        String refreshToken = getTokenFromRequest(TokenType.REFRESH_TOKEN, request);

        if (ObjectUtils.anyNull(accessToken, refreshToken)) {
            filterChain.doFilter(request, response);
            return;
        }

        Authentication authentication = attemptAuthentication(request, accessToken);

        if (Objects.nonNull(authentication)) {
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } else {
            String refreshedAccessToken = tokenService.refreshAccessToken(accessToken, refreshToken);
            saveRefreshedAccessTokenToCookie(response, refreshedAccessToken);

            Authentication refreshedAuthentication = attemptAuthentication(request, refreshedAccessToken);
            SecurityContextHolder.getContext().setAuthentication(refreshedAuthentication);
        }

        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(TokenType tokenType, HttpServletRequest request) {
        Cookie tokenCookie = CookieUtils.getCookie(request, tokenProperties.getTokenCookieKey(tokenType))
                .orElseGet(() -> new Cookie("empty", ""));
        String token = tokenCookie.getValue();

        return StringUtils.defaultIfBlank(token, null);
    }

    private Authentication attemptAuthentication(HttpServletRequest request, String accessToken) throws AuthenticationFailedException {
        if (tokenService.validateToken(TokenType.ACCESS_TOKEN, accessToken)) {
            SocialUser pseudoSocialUser = tokenService.getPseudoSocialUserFromToken(TokenType.ACCESS_TOKEN, accessToken);
            OAuthUserPrincipal userPrincipal = new OAuthUserPrincipal(pseudoSocialUser);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetails(request));

            return authentication;
        }

        return null;
    }

    private void saveRefreshedAccessTokenToCookie(HttpServletResponse response, String refreshedAccessToken) {
        String cookieKey = tokenProperties.getTokenCookieKey(TokenType.ACCESS_TOKEN);
        int cookieMaxAge = tokenProperties.getTokenCookieMaxAge(TokenType.ACCESS_TOKEN);

        CookieUtils.setCookie(response, cookieKey, refreshedAccessToken, cookieMaxAge);
    }

}
