package com.example.oauth.model;

import com.example.oauth.config.oauth.OAuthProvider;
import com.example.oauth.repository.model.SocialUser;
import lombok.ToString;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Date;
import java.util.Map;

@ToString
public class OAuthUserAttributes {
    private final OAuthProvider provider;
    private final UserRole role;
    private String username;
    private String email;
    private String profileImage;

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
            default:
                break;
        }
    }

    @SuppressWarnings("SwitchStatementWithTooFewBranches")
    public OAuthUserAttributes(OAuthProvider provider, Map<String, Object> userMap, UserRole role) {
        this.provider = provider;
        this.role = role;

        switch (provider) {
            case TWITTER:
                initTwitterOAuthUserAttributes(userMap);
                break;
            default:
                break;
        }
    }

    @SuppressWarnings("unchecked")
    private void initKakaoOAuthUserAttributes(OAuth2User user) {
        Map<String, Object> kakaoAccount = user.getAttribute("kakao_account");
        assert kakaoAccount != null;
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
        this.username = String.valueOf(profile.get("nickname"));
        this.email = String.valueOf(kakaoAccount.get("email"));
        this.profileImage = String.valueOf(profile.get("profile_image"));
    }

    private void initNaverOAuthUserAttributes(OAuth2User user) {
        Map<String, Object> response = user.getAttribute("response");
        assert response != null;
        this.username = String.valueOf(response.get("name"));
        this.email = String.valueOf(response.get("email"));
        this.profileImage = String.valueOf(response.get("profile_image"));
    }

    private void initTwitterOAuthUserAttributes(Map<String, Object> userMap) {
        this.username = String.valueOf(userMap.get("screen_name"));
        this.email = String.valueOf(userMap.get("email"));
        this.profileImage = String.valueOf(userMap.get("profile_image_url"));
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