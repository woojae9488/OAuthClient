package com.kwj.oauth.business.security.application;

import com.kwj.oauth.business.security.model.OAuthProvider;
import com.kwj.oauth.business.security.model.OAuthUserPrincipal;
import com.kwj.oauth.exception.OAuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OAuth2UserService extends DefaultOAuth2UserService {

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User defaultOAuth2User = super.loadUser(userRequest);
        OAuthProvider provider = Optional.of(userRequest.getClientRegistration())
                .map(ClientRegistration::getRegistrationId)
                .map(OAuthProvider::nameOf)
                .orElseThrow(() -> new OAuthException(
                        String.format("Failed to load oauth2 user: Not found OAuthProvider (%s)",
                                userRequest.getClientRegistration()), HttpStatus.BAD_REQUEST));

        return new OAuthUserPrincipal(provider, defaultOAuth2User);
    }

}
