package com.kwj.oauth.business.security.application;

import com.kwj.oauth.business.security.model.OAuthProvider;
import com.kwj.oauth.business.security.model.OAuthUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OAuth2UserService extends DefaultOAuth2UserService {

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuthProvider provider = OAuthProvider.nameOf(registrationId);
        OAuth2User defaultOAuth2User = super.loadUser(userRequest);
        return new OAuthUserPrincipal(provider, defaultOAuth2User);
    }

}
