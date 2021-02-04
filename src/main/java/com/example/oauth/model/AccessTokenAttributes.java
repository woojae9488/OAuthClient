package com.example.oauth.model;

import com.example.oauth.config.oauth.OAuthProvider;
import com.example.oauth.repository.model.SocialUser;
import io.jsonwebtoken.Claims;
import lombok.Getter;

@Getter
public class AccessTokenAttributes extends TokenAttributes {
    private Long id;
    private OAuthProvider provider;
    private String email;
    private UserRole role;

    private AccessTokenAttributes() {
        super(AuthenticationTokenType.ACCESS);
    }

    protected static AccessTokenAttributes of(SocialUser socialUser) {
        AccessTokenAttributes tokenAttributes = new AccessTokenAttributes();
        tokenAttributes.id = socialUser.getId();
        tokenAttributes.provider = socialUser.getProvider();
        tokenAttributes.email = socialUser.getEmail();
        tokenAttributes.role = socialUser.getRole();
        return tokenAttributes;
    }

    protected static AccessTokenAttributes of(Claims claims) {
        AccessTokenAttributes tokenAttributes = new AccessTokenAttributes();
        tokenAttributes.id = claims.get("id", Long.class);
        tokenAttributes.provider = claims.get("provider", OAuthProvider.class);
        tokenAttributes.email = claims.get("email", String.class);
        tokenAttributes.role = claims.get("role", UserRole.class);
        return tokenAttributes;
    }

    @Override
    public SocialUser getPseudoSocialUser() {
        return SocialUser.builder()
                .id(id)
                .provider(provider)
                .email(email)
                .role(role)
                .build();
    }
}
