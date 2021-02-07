package com.example.oauth.model.oauth;

import com.example.oauth.model.UserRole;
import com.example.oauth.repository.model.SocialUser;
import com.example.oauth.util.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Getter
@ToString
public class OAuthUserPrincipal implements OAuth2User, UserDetails {

    private final Collection<? extends GrantedAuthority> authorities;
    @Setter
    private SocialUser socialUser;

    public OAuthUserPrincipal(OAuthProvider provider, OAuth2User user) {
        this.authorities = user.getAuthorities();
        this.socialUser = new OAuthUserAttributes(provider, user, UserRole.USER).generateSocialUser();
    }

    public OAuthUserPrincipal(OAuthProvider provider, Map<String, Object> userMap) {
        // TODO : add UserRole check logic
        this.authorities = Collections.singletonList(new SimpleGrantedAuthority(UserRole.USER.getRoleType()));
        this.socialUser = new OAuthUserAttributes(provider, userMap, UserRole.USER).generateSocialUser();
    }

    public OAuthUserPrincipal(SocialUser socialUser) {
        String roleType = socialUser.getRole().getRoleType();
        this.authorities = Collections.singletonList(new SimpleGrantedAuthority(roleType));
        this.socialUser = socialUser;
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

    @Override
    public Map<String, Object> getAttributes() {
        return JsonUtils.convertValue(socialUser, new TypeReference<Map<String, Object>>() {
        }).orElseGet(HashMap::new);
    }
}
