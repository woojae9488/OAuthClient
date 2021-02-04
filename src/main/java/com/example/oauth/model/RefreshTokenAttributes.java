package com.example.oauth.model;

import com.example.oauth.config.oauth.OAuthProvider;
import com.example.oauth.repository.model.SocialUser;
import io.jsonwebtoken.Claims;
import lombok.Getter;

@Getter
public class RefreshTokenAttributes extends TokenAttributes {
    private Long id;
    private OAuthProvider provider;
    private String email;
    // TODO : discuss token attributes

    private RefreshTokenAttributes() {
        super(AuthenticationTokenType.REFRESH);
    }

    protected static RefreshTokenAttributes of(SocialUser socialUser) {
        RefreshTokenAttributes tokenAttributes = new RefreshTokenAttributes();
        tokenAttributes.id = socialUser.getId();
        tokenAttributes.provider = socialUser.getProvider();
        tokenAttributes.email = socialUser.getEmail();
        return tokenAttributes;
    }

    protected static RefreshTokenAttributes of(Claims claims) {
        RefreshTokenAttributes tokenAttributes = new RefreshTokenAttributes();
        tokenAttributes.id = claims.get("id", Long.class);
        tokenAttributes.provider = claims.get("provider", OAuthProvider.class);
        tokenAttributes.email = claims.get("email", String.class);
        return tokenAttributes;
    }

    @Override
    public SocialUser getPseudoSocialUser() {
        return SocialUser.builder()
                .id(id)
                .provider(provider)
                .email(email)
                .build();
    }
}
