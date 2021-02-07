package com.example.oauth.config.properties;

import com.example.oauth.model.oauth.OAuthProvider;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

import java.util.List;

@Getter
@Setter
@ToString
public class OAuth2ClientProperties {
    private OAuthProvider provider;
    private String clientId;
    private String clientSecret;
    private List<String> scopes;
    private String authorizationUri;
    private String tokenUri;
    private String userInfoUri;
    private String userNameAttribute;

    public OAuth2ClientProperties(OAuthProvider provider) {
        this.provider = provider;
    }

    public ClientRegistration generateRegistration() {
        return ClientRegistration.withRegistrationId(provider.getName())
                .clientId(clientId)
                .clientSecret(clientSecret)
                .clientAuthenticationMethod(ClientAuthenticationMethod.POST)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                .scope(scopes)
                .clientName(provider.getName())
                .authorizationUri(authorizationUri)
                .tokenUri(tokenUri)
                .userInfoUri(userInfoUri)
                .userNameAttributeName(userNameAttribute)
                .build();
    }
}
