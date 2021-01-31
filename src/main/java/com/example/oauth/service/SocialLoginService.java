package com.example.oauth.service;

import com.example.oauth.config.oauth.OAuth1ClientProperties;
import com.example.oauth.model.OAuth1Requirement;
import lombok.RequiredArgsConstructor;
import org.springframework.social.oauth1.AuthorizedRequestToken;
import org.springframework.social.oauth1.OAuth1Operations;
import org.springframework.social.oauth1.OAuth1Parameters;
import org.springframework.social.oauth1.OAuthToken;
import org.springframework.social.twitter.api.impl.TwitterTemplate;
import org.springframework.social.twitter.connect.TwitterConnectionFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SocialLoginService {
    private final OAuth1ClientProperties twitterClientProperties;

    private OAuth1Operations twitterOAuthOperations;

    @PostConstruct
    public void initialize() {
        twitterOAuthOperations = new TwitterConnectionFactory(
                twitterClientProperties.getClientId(),
                twitterClientProperties.getClientSecret()
        ).getOAuthOperations();
    }

    public OAuth1Requirement getTwitterOAuthRequirement() {
        OAuthToken requestToken = twitterOAuthOperations.fetchRequestToken(twitterClientProperties.getCallbackUri(), null);
        String authenticationUri = twitterOAuthOperations.buildAuthenticateUrl(requestToken.getValue(), new OAuth1Parameters());
        return OAuth1Requirement.builder()
                .requestToken(requestToken)
                .authenticationUri(authenticationUri)
                .build();
    }

    public TwitterTemplate getTwitterTemplate(OAuthToken requestToken, String oauthVerifier) {
        AuthorizedRequestToken authorizedRequestToken = new AuthorizedRequestToken(requestToken, oauthVerifier);
        OAuthToken accessToken = twitterOAuthOperations.exchangeForAccessToken(authorizedRequestToken, null);
        return new TwitterTemplate(
                twitterClientProperties.getClientId(),
                twitterClientProperties.getClientSecret(),
                accessToken.getValue(),
                accessToken.getSecret()
        );
    }

    public Object getUserInformationMap(TwitterTemplate twitterTemplate) {
        return twitterTemplate.restOperations().getForObject(twitterClientProperties.getUserInfoUri(), Object.class);
    }


//    public void setAuthentication(Map<String, String> map) {
//        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(map.get("id"),
//                "N/A", Arrays.asList(new GrantedAuthority[]{new SimpleGrantedAuthority(SocialType.TWITTER.getRoleType())}));
//        authenticationToken.setDetails(map);
//        OAuth2Request oAuth2Request = new OAuth2Request(null, map.get("id"), null, true, null,
//                null, null, null, null);
//        Authentication authentication = new OAuth2Authentication(oAuth2Request, authenticationToken);
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//    }
//
//    public void saveUserIfNotExist(Connection<Twitter> connection, Map<String, String> map) {
//        if (userService.isNotExistUser(map.get("id"))) {
//            userService.saveUser(User.builder()
//                    .userPrincipal(map.get("id"))
//                    .userName(map.get("name"))
//                    .userEmail(connection.fetchUserProfile().getEmail())
//                    .userImage(connection.getImageUrl())
//                    .socialType(SocialType.TWITTER)
//                    .build());
//        }
//    }

}
