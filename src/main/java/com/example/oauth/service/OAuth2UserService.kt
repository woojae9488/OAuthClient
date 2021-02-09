package com.example.oauth.service

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import kotlin.Throws
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.user.OAuth2User
import com.example.oauth.model.oauth.OAuthProvider
import com.example.oauth.model.oauth.OAuthUserPrincipal
import org.springframework.stereotype.Service

@Service
class OAuth2UserService : DefaultOAuth2UserService() {

    @Throws(OAuth2AuthenticationException::class)
    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        val registrationId = userRequest.clientRegistration.registrationId
        val provider = OAuthProvider.idOf(registrationId)!!
        val defaultOAuth2User = super.loadUser(userRequest)
        return OAuthUserPrincipal(provider, defaultOAuth2User)
    }

}