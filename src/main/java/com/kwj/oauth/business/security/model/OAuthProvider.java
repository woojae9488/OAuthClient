package com.kwj.oauth.business.security.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum OAuthProvider {

    KAKAO("kakao"),
    NAVER("naver"),
    TWITTER("twitter");

    private final String name;

    public static OAuthProvider nameOf(String name) {
        return Arrays.stream(values())
                .filter(provider -> StringUtils.equals(provider.name, name))
                .findFirst()
                .orElse(null);
    }

}
