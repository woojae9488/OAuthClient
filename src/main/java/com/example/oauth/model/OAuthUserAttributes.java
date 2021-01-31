package com.example.oauth.model;

import com.example.oauth.config.oauth.OAuthProvider;
import com.example.oauth.repository.model.SocialUser;
import lombok.ToString;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Date;
import java.util.Map;

@ToString
public class OAuthUserAttributes {
    private OAuthProvider provider;
    private String username;
    private String email;
    private String profileImage;
    private UserRole role;

    public OAuthUserAttributes(OAuthProvider provider, OAuth2User user, UserRole role) {
        this.provider = provider;
        this.role = role;

        switch (provider) {
            case KAKAO:
                initKakaoOAuthUserAttributes(user);
                break;
            case NAVER:
                initNaverOAuthUserAttributes(user);
                break;
            case TWITTER:
                initTwitterOAuthUserAttributes(user);
                break;
            default:
                break;
        }
    }

    private void initKakaoOAuthUserAttributes(OAuth2User user) {
        this.username = user.getAttribute("username");
        this.email = user.getAttribute("email");
        this.profileImage = user.getAttribute("profile_image");
    }

    private void initNaverOAuthUserAttributes(OAuth2User user) {
        Map<String, Object> response = user.getAttribute("response");
        this.username = String.valueOf(response.get("name"));
        this.email = String.valueOf(response.get("email"));
        this.profileImage = String.valueOf(response.get("profile_image"));
    }

    private void initTwitterOAuthUserAttributes(OAuth2User user) {
        this.username = user.getAttribute("username");
        this.email = user.getAttribute("email");
        this.profileImage = user.getAttribute("profile_image");
    }

    public SocialUser generateSocialUser() {
        return SocialUser.builder()
                .provider(provider)
                .username(username)
                .email(email)
                .profileImage(profileImage)
                .role(role)
                .createTime(new Date().getTime())
                .build();
    }
}