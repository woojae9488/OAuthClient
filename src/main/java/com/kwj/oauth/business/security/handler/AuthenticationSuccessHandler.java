package com.kwj.oauth.business.security.handler;

import com.kwj.oauth.business.security.model.OAuthUserPrincipal;
import com.kwj.oauth.business.token.application.AuthorizationTokenService;
import com.kwj.oauth.business.token.model.TokenType;
import com.kwj.oauth.config.properties.AuthorizationTokenProperties;
import com.kwj.oauth.business.user.infra.SocialUserRepository;
import com.kwj.oauth.business.user.domain.SocialUser;
import com.kwj.oauth.util.CookieUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    public static final String OAUTH_SUCCESS_REDIRECT_URL = "/";

    private final SocialUserRepository userRepository;
    private final AuthorizationTokenService tokenService;
    private final AuthorizationTokenProperties tokenProperties;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        if (response.isCommitted()) {
            return;
        }

        OAuthUserPrincipal userPrincipal = (OAuthUserPrincipal) authentication.getPrincipal();
        SocialUser socialUser = userPrincipal.getSocialUser();
        Optional<SocialUser> socialUserEntity = getSocialUserEntity(socialUser);
        socialUserEntity.ifPresentOrElse(
                userPrincipal::setSocialUser,
                () -> userRepository.save(socialUser)
        );

        setAuthorizationTokenCookies(response, userPrincipal.getSocialUser());
        getRedirectStrategy().sendRedirect(request, response, OAUTH_SUCCESS_REDIRECT_URL);
    }

    private Optional<SocialUser> getSocialUserEntity(SocialUser socialUser) {
        return userRepository.findByProviderAndProviderUserId(socialUser.getProvider(), socialUser.getProviderUserId());
    }

    private void setAuthorizationTokenCookies(HttpServletResponse response, SocialUser socialUser) {
        String accessToken = tokenService.createAccessToken(socialUser);
        String refreshToken = tokenService.createRefreshToken(socialUser, accessToken);

        saveTokenToCookie(response, TokenType.ACCESS_TOKEN, accessToken);
        saveTokenToCookie(response, TokenType.REFRESH_TOKEN, refreshToken);
    }

    private void saveTokenToCookie(HttpServletResponse response, TokenType tokenType, String token) {
        String cookieKey = tokenProperties.getTokenCookieKey(tokenType);
        int cookieMaxAge = tokenProperties.getTokenCookieMaxAge(tokenType);

        CookieUtils.setCookie(response, cookieKey, token, cookieMaxAge);
    }

}
