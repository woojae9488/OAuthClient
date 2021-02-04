package com.example.oauth.model;

import com.example.oauth.repository.model.SocialUser;
import com.example.oauth.util.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import io.jsonwebtoken.Claims;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public abstract class TokenAttributes {
    private TokenType type;

    protected TokenAttributes(TokenType type) {
        this.type = type;
    }

    public static TokenAttributes extract(TokenType tokenType, SocialUser socialUser) {
        switch (tokenType) {
            case ACCESS_TOKEN:
                return AccessTokenAttributes.of(socialUser);
            case REFRESH_TOKEN:
                return RefreshTokenAttributes.of(socialUser);
            default:
                return null;
        }
    }

    public static TokenAttributes restore(TokenType tokenType, Claims claims) {
        switch (tokenType) {
            case ACCESS_TOKEN:
                return AccessTokenAttributes.of(claims);
            case REFRESH_TOKEN:
                return RefreshTokenAttributes.of(claims);
            default:
                return null;
        }
    }

    public Map<String, Object> getAttributesMap() {
        return JsonUtils.convertValue(this, new TypeReference<Map<String, Object>>() {
        }).orElse(new HashMap<>());
    }

    public abstract SocialUser getPseudoSocialUser();
}
