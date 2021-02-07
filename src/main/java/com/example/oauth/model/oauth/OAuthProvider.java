package com.example.oauth.model.oauth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OAuthProvider {
    KAKAO("kakao"),
    NAVER("naver"),
    TWITTER("twitter");

    private final String name;

    public static OAuthProvider nameOf(String name) {
        for (OAuthProvider provider : values()) {
            if (name.equals(provider.getName())) {
                return provider;
            }
        }
        return null;
    }
}
