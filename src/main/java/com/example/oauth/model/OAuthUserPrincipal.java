package com.example.oauth.model;

import com.example.oauth.config.oauth.OAuthProvider;
import com.example.oauth.repository.model.SocialUser;
import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Getter
@ToString
public class OAuthUserPrincipal implements OAuth2User, UserDetails {

    private final Collection<? extends GrantedAuthority> authorities;
    private final Map<String, Object> attributes;
    private final SocialUser socialUser;

    public OAuthUserPrincipal(OAuthProvider provider, OAuth2User user) {
        authorities = user.getAuthorities();
        attributes = new HashMap<>();
        socialUser = new OAuthUserAttributes(provider, user, UserRole.USER).generateSocialUser();
    }

    @Override
    public String getName() {
        return getUsername();
    }

    @Override
    public String getUsername() {
        return socialUser.getUsername();
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}