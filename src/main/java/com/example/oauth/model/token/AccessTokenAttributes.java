package com.example.oauth.model.token;

import com.example.oauth.model.UserRole;
import com.example.oauth.model.oauth.OAuthProvider;
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
        super(TokenType.ACCESS_TOKEN);
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
        tokenAttributes.provider = OAuthProvider.valueOf(claims.get("provider", String.class));
        tokenAttributes.email = claims.get("email", String.class);
        tokenAttributes.role = UserRole.valueOf(claims.get("role", String.class));
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
