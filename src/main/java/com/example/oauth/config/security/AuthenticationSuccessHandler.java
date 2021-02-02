package com.example.oauth.config.security;

import com.example.oauth.model.OAuthUserPrincipal;
import com.example.oauth.repository.SocialUserRepository;
import com.example.oauth.repository.model.SocialUser;
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
//    private final TokenProvider tokenProvider;

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

//        String token = tokenProvider.createToken(authentication);
//        CookieUtils.setCookie(response, TokenProperties.COOKIE_STRING, token, TokenProperties.COOKIE_MAX_AGE);
        getRedirectStrategy().sendRedirect(request, response, OAUTH_SUCCESS_REDIRECT_URL);
    }

    private SocialUser getSocialUserEntity(SocialUser socialUser) {
        return userRepository.findByProviderAndUsername(socialUser.getProvider(), socialUser.getUsername());
    }
}
