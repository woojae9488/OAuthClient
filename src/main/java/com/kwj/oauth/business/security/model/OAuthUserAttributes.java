package com.kwj.oauth.business.security.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.kwj.oauth.business.user.domain.SocialUser;
import com.kwj.oauth.util.JsonUtils;
import lombok.ToString;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collections;
import java.util.Map;

@ToString
public class OAuthUserAttributes {

    private final OAuthProvider provider;
    private final UserRole role;
    private Long providerUserId;
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

    public OAuthUserAttributes(OAuthProvider provider, Map<String, Object> userMap, UserRole role) {
        this.provider = provider;
        this.role = role;

        if (provider == OAuthProvider.TWITTER) {
            initTwitterOAuthUserAttributes(userMap);
        }
    }

    private void initKakaoOAuthUserAttributes(OAuth2User user) {
        Map<String, Object> kakaoAccount = user.getAttribute("kakao_account");
        Integer providerUserIdInt = user.getAttribute("id");
        assert kakaoAccount != null && providerUserIdInt != null;
        Map<String, Object> profile = JsonUtils.convertValue(kakaoAccount.get("profile"),
                new TypeReference<Map<String, Object>>() {
                }).orElseGet(Collections::emptyMap);

        this.providerUserId = providerUserIdInt.longValue();
        this.username = String.valueOf(profile.get("nickname"));
        this.email = String.valueOf(kakaoAccount.get("email"));
        this.profileImage = String.valueOf(profile.get("profile_image"));
    }

    private void initNaverOAuthUserAttributes(OAuth2User user) {
        Map<String, Object> response = user.getAttribute("response");
        assert response != null;

        this.providerUserId = Long.valueOf(String.valueOf(response.get("id")));
        this.username = String.valueOf(response.get("name"));
        this.email = String.valueOf(response.get("email"));
        this.profileImage = String.valueOf(response.get("profile_image"));
    }

    private void initTwitterOAuthUserAttributes(Map<String, Object> userMap) {
        this.providerUserId = Long.valueOf(String.valueOf(userMap.get("id")));
        this.username = String.valueOf(userMap.get("name"));
        this.email = String.valueOf(userMap.get("email"));
        this.profileImage = String.valueOf(userMap.get("profile_image_url"));
    }

    public SocialUser generateSocialUser() {
        return SocialUser.builder()
                .provider(provider)
                .providerUserId(providerUserId)
                .username(username)
                .email(email)
                .profileImage(profileImage)
                .role(role)
                .createTime(System.currentTimeMillis())
                .build();
    }

}