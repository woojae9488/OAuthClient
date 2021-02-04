package com.example.oauth.config.security;

import com.example.oauth.config.token.AuthenticationTokenProperties;
import com.example.oauth.model.AuthenticationTokenType;
import com.example.oauth.model.OAuthUserPrincipal;
import com.example.oauth.repository.SocialUserRepository;
import com.example.oauth.repository.model.SocialUser;
import com.example.oauth.service.AuthenticationTokenService;
import com.example.oauth.util.CookieUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    public static final String OAUTH_SUCCESS_REDIRECT_URL = "/";

    private final SocialUserRepository userRepository;
    private final AuthenticationTokenService tokenService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        if (response.isCommitted()) {
            return;
        }

        OAuthUserPrincipal userPrincipal = (OAuthUserPrincipal) authentication.getPrincipal();
        SocialUser socialUser = userPrincipal.getSocialUser();
        SocialUser entity = getSocialUserEntity(socialUser);
        if (entity == null) {
            userRepository.save(socialUser);
        } else {
            userPrincipal.setSocialUser(entity);
        }

        String accessToken = tokenService.createToken(AuthenticationTokenType.ACCESS, socialUser);
        saveTokenToCookie(response, AuthenticationTokenType.ACCESS, accessToken);
        String refreshToken = tokenService.createToken(AuthenticationTokenType.REFRESH, socialUser);
        saveTokenToCookie(response, AuthenticationTokenType.REFRESH, refreshToken);

        getRedirectStrategy().sendRedirect(request, response, OAUTH_SUCCESS_REDIRECT_URL);
    }

    private SocialUser getSocialUserEntity(SocialUser socialUser) {
        return userRepository.findByProviderAndEmail(socialUser.getProvider(), socialUser.getUsername());
    }

    private void saveTokenToCookie(HttpServletResponse response, AuthenticationTokenType tokenType, String token) {
        String cookieKey;
        int cookieMaxAge;

        switch (tokenType) {
            case ACCESS:
                cookieKey = AuthenticationTokenProperties.ACCESS_TOKEN_COOKIE_STRING;
                cookieMaxAge = AuthenticationTokenProperties.ACCESS_TOKEN_COOKIE_MAX_AGE;
                break;
            case REFRESH:
                cookieKey = AuthenticationTokenProperties.REFRESH_TOKEN_COOKIE_STRING;
                cookieMaxAge = AuthenticationTokenProperties.REFRESH_TOKEN_COOKIE_MAX_AGE;
                break;
            default:
                cookieKey = "";
                cookieMaxAge = 0;
        }

        CookieUtils.setCookie(response, cookieKey, token, cookieMaxAge);
    }
}
