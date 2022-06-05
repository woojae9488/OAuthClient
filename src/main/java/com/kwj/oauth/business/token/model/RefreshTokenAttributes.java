package com.kwj.oauth.business.token.model;

import com.kwj.oauth.business.user.domain.SocialUser;
import io.jsonwebtoken.Claims;
import lombok.Getter;

@Getter
public class RefreshTokenAttributes extends TokenAttributes {

    private Long id;

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
