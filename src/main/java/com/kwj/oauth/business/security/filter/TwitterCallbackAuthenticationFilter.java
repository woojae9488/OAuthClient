package com.kwj.oauth.business.security.filter;

import com.kwj.oauth.config.properties.OAuth1ClientProperties;
import com.kwj.oauth.business.security.model.OAuthProvider;
import com.kwj.oauth.business.security.model.OAuthUserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
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

    @Autowired
    private OAuth1ClientProperties clientProperties;
    @Autowired
    private OAuth1Operations oauthOperations;

    protected TwitterCallbackAuthenticationFilter() {
        super(CALLBACK_PROCESSING_URL);
    }

    @Override
    @Autowired
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        super.setAuthenticationManager(authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        OAuthToken requestToken = (OAuthToken) request.getServletContext().getAttribute("token");
        request.getServletContext().removeAttribute("token");
        String oauthVerifier = request.getParameter("oauth_verifier");

        TwitterTemplate twitterTemplate = generateTwitterTemplate(requestToken, oauthVerifier);
        OAuthUserPrincipal userPrincipal = generateOAuthUserPrincipal(twitterTemplate);
        return generateAuthentication(request, userPrincipal);
    }

    private TwitterTemplate generateTwitterTemplate(OAuthToken requestToken, String oauthVerifier) {
        AuthorizedRequestToken authorizedRequestToken = new AuthorizedRequestToken(requestToken, oauthVerifier);
        OAuthToken accessToken = oauthOperations.exchangeForAccessToken(authorizedRequestToken, null);
        return new TwitterTemplate(
                clientProperties.getClientId(), clientProperties.getClientSecret(),
                accessToken.getValue(), accessToken.getSecret()
        );
    }

    @SuppressWarnings("unchecked")
    public OAuthUserPrincipal generateOAuthUserPrincipal(TwitterTemplate twitterTemplate) {
        Map<String, Object> userMap = (Map<String, Object>) twitterTemplate.restOperations()
                .getForObject(clientProperties.getUserInfoUri(), Map.class);
        return new OAuthUserPrincipal(OAuthProvider.TWITTER, userMap);
    }

    private Authentication generateAuthentication(HttpServletRequest request, OAuthUserPrincipal userPrincipal) {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetails(request));
        return authentication;
    }

}
