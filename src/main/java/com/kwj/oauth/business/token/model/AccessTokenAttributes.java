package com.kwj.oauth.business.token.model;

import com.kwj.oauth.business.security.model.UserRole;
import com.kwj.oauth.business.security.model.OAuthProvider;
import com.kwj.oauth.business.user.domain.SocialUser;
import io.jsonwebtoken.Claims;
import lombok.Getter;

@Getter
public class AccessTokenAttributes extends TokenAttributes {

    private Long id;
    private OAuthProvider provider;
    private String username;
    private String profileImage;
    private UserRole role;

    private AccessTokenAttributes() {
        super(TokenType.ACCESS_TOKEN);
    }

    protected static AccessTokenAttributes of(SocialUser socialUser) {
        AccessTokenAttributes tokenAttributes = new AccessTokenAttributes();
        tokenAttributes.id = socialUser.getId();
        tokenAttributes.provider = socialUser.getProvider();
        tokenAttributes.username = socialUser.getUsername();
        tokenAttributes.profileImage = socialUser.getProfileImage();
        tokenAttributes.role = socialUser.getRole();
        return tokenAttributes;
    }

    protected static AccessTokenAttributes of(Claims claims) {
        AccessTokenAttributes tokenAttributes = new AccessTokenAttributes();
        tokenAttributes.id = claims.get("id", Long.class);
        tokenAttributes.provider = OAuthProvider.valueOf(claims.get("provider", String.class));
        tokenAttributes.username = claims.get("username", String.class);
        tokenAttributes.profileImage = claims.get("profileImage", String.class);
        tokenAttributes.role = UserRole.valueOf(claims.get("role", String.class));
        return tokenAttributes;
    }

    @Override
    public SocialUser getPseudoSocialUser() {
        return SocialUser.builder()
                .id(id)
                .provider(provider)
                .username(username)
                .profileImage(profileImage)
                .role(role)
                .build();
    }

}