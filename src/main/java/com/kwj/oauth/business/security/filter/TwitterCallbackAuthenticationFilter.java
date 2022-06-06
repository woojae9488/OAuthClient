package com.kwj.oauth.business.security.filter;

import com.kwj.oauth.business.security.application.SecurityContextHelper;
import com.kwj.oauth.business.security.application.ServletContextHelper;
import com.kwj.oauth.business.security.model.OAuthProvider;
import com.kwj.oauth.business.security.model.OAuthUserPrincipal;
import com.kwj.oauth.config.properties.OAuth1ClientProperties;
import com.kwj.oauth.exception.OAuthException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.social.oauth1.AuthorizedRequestToken;
import org.springframework.social.oauth1.OAuth1Operations;
import org.springframework.social.oauth1.OAuthToken;
import org.springframework.social.twitter.api.impl.TwitterTemplate;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Component
public class TwitterCallbackAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private static final String CALLBACK_PROCESSING_URL = "/login/oauth1/twitter/callback";

    private final OAuth1ClientProperties clientProperties;
    private final OAuth1Operations oauthOperations;

    protected TwitterCallbackAuthenticationFilter(
            OAuth1ClientProperties clientProperties,
            OAuth1Operations oauthOperations
    ) {
        super(CALLBACK_PROCESSING_URL);

        this.clientProperties = clientProperties;
        this.oauthOperations = oauthOperations;
    }

    @Autowired
    @Override
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        super.setAuthenticationManager(authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        OAuthToken requestToken = ServletContextHelper.getAttributeWithRemove(request, "token", OAuthToken.class)
                .orElseThrow(() -> new OAuthException("Failed to attempt authentication: Not found token attribute"));
        String oauthVerifier = request.getParameter("oauth_verifier");

        TwitterTemplate twitterTemplate = generateTwitterTemplate(requestToken, oauthVerifier);
        OAuthUserPrincipal userPrincipal = getOAuthUserPrincipal(twitterTemplate);

        return SecurityContextHelper.generateAuthentication(request, userPrincipal);
    }

    private TwitterTemplate generateTwitterTemplate(OAuthToken requestToken, String oauthVerifier) {
        AuthorizedRequestToken authorizedRequestToken = new AuthorizedRequestToken(requestToken, oauthVerifier);
        OAuthToken accessToken = oauthOperations.exchangeForAccessToken(authorizedRequestToken, null);

        return new TwitterTemplate(
                clientProperties.getClientId(),
                clientProperties.getClientSecret(),
                accessToken.getValue(),
                accessToken.getSecret()
        );
    }

    @SuppressWarnings("unchecked")
    public OAuthUserPrincipal getOAuthUserPrincipal(TwitterTemplate twitterTemplate) {
        Map<String, Object> userMap = (Map<String, Object>) twitterTemplate.restOperations()
                .getForObject(clientProperties.getUserInfoUri(), Map.class);

        return new OAuthUserPrincipal(OAuthProvider.TWITTER, userMap);
    }

}
