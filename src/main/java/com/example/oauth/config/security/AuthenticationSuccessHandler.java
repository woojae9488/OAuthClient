package com.example.oauth.config.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    public static final String OAUTH_SUCCESS_REDIRECT_URL = "/";

//    @Autowired
//    private TokenProvider tokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        if (response.isCommitted()) {
            return;
        }

//        String token = tokenProvider.createToken(authentication);
//        CookieUtils.setCookie(response, TokenProperties.COOKIE_STRING, token, TokenProperties.COOKIE_MAX_AGE);
        getRedirectStrategy().sendRedirect(request, response, OAUTH_SUCCESS_REDIRECT_URL);
    }

}
