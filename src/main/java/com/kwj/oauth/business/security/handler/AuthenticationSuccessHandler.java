package com.kwj.oauth.business.security.handler;

import com.kwj.oauth.business.security.application.SecurityContextHelper;
import com.kwj.oauth.business.security.model.OAuthUserPrincipal;
import com.kwj.oauth.business.token.application.AuthorizationTokenService;
import com.kwj.oauth.business.token.application.TokenCookieService;
import com.kwj.oauth.business.token.model.TokenType;
import com.kwj.oauth.business.user.domain.SocialUser;
import com.kwj.oauth.business.user.infra.SocialUserRepository;
import com.kwj.oauth.exception.OAuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    public static final String OAUTH_SUCCESS_REDIRECT_URL = "/";

    private final AuthorizationTokenService tokenService;
    private final TokenCookieService tokenCookieService;
    private final SocialUserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        if (response.isCommitted()) {
            return;
        }

        OAuthUserPrincipal userPrincipal = SecurityContextHelper.getOAuthUserPrincipal(authentication);
        if (Objects.isNull(userPrincipal)) {
            throw new OAuthException("Failed to get OAuthUserPrincipal", HttpStatus.UNAUTHORIZED);
        }

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

        tokenCookieService.saveTokenToCookie(response, TokenType.ACCESS_TOKEN, accessToken);
        tokenCookieService.saveTokenToCookie(response, TokenType.REFRESH_TOKEN, refreshToken);
    }

}
