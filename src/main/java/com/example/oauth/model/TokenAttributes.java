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
    private AuthenticationTokenType type;

    protected TokenAttributes(AuthenticationTokenType type) {
        this.type = type;
    }

    public static TokenAttributes extract(AuthenticationTokenType tokenType, SocialUser socialUser) {
        switch (tokenType) {
            case ACCESS:
                return AccessTokenAttributes.of(socialUser);
            case REFRESH:
                return RefreshTokenAttributes.of(socialUser);
            default:
                // This should not be called.
                return null;
        }
    }

    public static TokenAttributes restore(AuthenticationTokenType tokenType, Claims claims) {
        switch (tokenType) {
            case ACCESS:
                return AccessTokenAttributes.of(claims);
            case REFRESH:
                return RefreshTokenAttributes.of(claims);
            default:
                // This should not be called.
                return null;
        }
    }

    public Map<String, Object> getAttributesMap() {
        return JsonUtils.convertValue(this, new TypeReference<Map<String, Object>>() {
        }).orElse(new HashMap<>());
    }

    public abstract SocialUser getPseudoSocialUser();
}
