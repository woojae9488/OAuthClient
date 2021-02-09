package com.example.oauth.config.properties

import com.example.oauth.model.oauth.OAuthProvider
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.core.ClientAuthenticationMethod

class OAuth2ClientProperties(
    val provider: OAuthProvider,
    var clientId: String = "",
    var clientSecret: String = "",
    var scopes: List<String> = ArrayList(),
    var authorizationUri: String = "",
    var tokenUri: String = "",
    var userInfoUri: String = "",
    var userNameAttribute: String = "",
) {

    fun generateRegistration(): ClientRegistration {
        return ClientRegistration.withRegistrationId(provider.id)
            .clientId(clientId)
            .clientSecret(clientSecret)
            .clientAuthenticationMethod(ClientAuthenticationMethod.POST)
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
            .scope(scopes)
            .clientName(provider.id)
            .authorizationUri(authorizationUri)
            .tokenUri(tokenUri)
            .userInfoUri(userInfoUri)
            .userNameAttributeName(userNameAttribute)
            .build()
    }

}