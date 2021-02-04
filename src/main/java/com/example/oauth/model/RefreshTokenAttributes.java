package com.example.oauth.model;

import com.example.oauth.repository.model.SocialUser;
import io.jsonwebtoken.Claims;
import lombok.Getter;

@Getter
public class RefreshTokenAttributes extends TokenAttributes {
    private Long id;
    // TODO : discuss token attributes

    private RefreshTokenAttributes() {
        super(TokenType.REFRESH_TOKEN);
    }

    protected static RefreshTokenAttributes of(SocialUser socialUser) {
        RefreshTokenAttributes tokenAttributes = new RefreshTokenAttributes();
        tokenAttributes.id = socialUser.getId();
        return tokenAttributes;
    }

    protected static RefreshTokenAttributes of(Claims claims) {
        RefreshTokenAttributes tokenAttributes = new RefreshTokenAttributes();
        tokenAttributes.id = claims.get("id", Long.class);
        return tokenAttributes;
    }

    @Override
    public SocialUser getPseudoSocialUser() {
        return SocialUser.builder()
                .id(id)
                .build();
    }
}
