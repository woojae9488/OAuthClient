package com.kwj.oauth.business.security.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.social.oauth1.OAuthToken;

@Getter
@Builder
@ToString
public class OAuth1Requirement {

    private final OAuthToken requestToken;
    private final String authenticationUri;

}
