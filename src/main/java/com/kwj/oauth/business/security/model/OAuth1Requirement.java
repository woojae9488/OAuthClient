package com.kwj.oauth.business.security.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.social.oauth1.OAuthToken;

@Data
@Builder
public class OAuth1Requirement {

    private OAuthToken requestToken;
    private String authenticationUri;

}
