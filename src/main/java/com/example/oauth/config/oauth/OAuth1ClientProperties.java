package com.example.oauth.config.oauth;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class OAuth1ClientProperties {
    private OAuthProvider provider;
    private String clientId;
    private String clientSecret;
    private String callbackUri;
    private String userInfoUri;
    private String userNameAttribute;

    public OAuth1ClientProperties(OAuthProvider provider) {
        this.provider = provider;
    }
}
