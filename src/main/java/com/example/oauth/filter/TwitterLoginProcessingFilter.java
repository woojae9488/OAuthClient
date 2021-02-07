package com.example.oauth.filter;

import com.example.oauth.config.properties.OAuth1ClientProperties;
import com.example.oauth.model.oauth.OAuth1Requirement;
import lombok.RequiredArgsConstructor;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.social.oauth1.OAuth1Operations;
import org.springframework.social.oauth1.OAuth1Parameters;
import org.springframework.social.oauth1.OAuthToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class TwitterLoginProcessingFilter extends OncePerRequestFilter {
    private static final String LOGIN_REQUEST_URL = "/oauth1/authorization/twitter";

    private final RequestMatcher requireLoginRequestMatcher = new AntPathRequestMatcher(LOGIN_REQUEST_URL);
    private final OAuth1ClientProperties clientProperties;
    private final OAuth1Operations oauthOperations;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        if (requiresLoginProcessing(request)) {
            OAuth1Requirement oauth1Requirement = getTwitterOAuthRequirement();
            request.getServletContext().setAttribute("token", oauth1Requirement.getRequestToken());
            response.sendRedirect(oauth1Requirement.getAuthenticationUri());
            return;
        }

        chain.doFilter(request, response);
    }

    private boolean requiresLoginProcessing(HttpServletRequest request) {
        return requireLoginRequestMatcher.matches(request);
    }

    private OAuth1Requirement getTwitterOAuthRequirement() {
        OAuthToken requestToken = oauthOperations.fetchRequestToken(clientProperties.getCallbackUri(), null);
        String authenticationUri = oauthOperations.buildAuthenticateUrl(requestToken.getValue(), new OAuth1Parameters());
        return OAuth1Requirement.builder()
                .requestToken(requestToken)
                .authenticationUri(authenticationUri)
                .build();
    }
}
