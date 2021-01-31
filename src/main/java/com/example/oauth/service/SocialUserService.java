package com.example.oauth.service;

import com.example.oauth.config.oauth.OAuthProvider;
import com.example.oauth.model.OAuthUserPrincipal;
import com.example.oauth.repository.model.SocialUser;
import com.example.oauth.repository.SocialUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SocialUserService extends DefaultOAuth2UserService {
    private final SocialUserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuthUserPrincipal userPrincipal = new OAuthUserPrincipal(OAuthProvider.nameOf(registrationId), super.loadUser(userRequest));

        SocialUser socialUser = userPrincipal.getSocialUser();
        if (isUserNotExist(socialUser)) {
            userRepository.save(socialUser);
        }
        return userPrincipal;
    }

    private boolean isUserNotExist(SocialUser socialUser) {
        return !userRepository.existsByProviderAndUsername(socialUser.getProvider(), socialUser.getUsername());
    }
}
