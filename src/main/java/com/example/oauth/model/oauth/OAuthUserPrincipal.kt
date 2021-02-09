package com.example.oauth.model.oauth

import com.example.oauth.model.UserRole
import com.example.oauth.repository.model.SocialUser
import com.example.oauth.util.JsonUtils
import com.fasterxml.jackson.core.type.TypeReference
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.oauth2.core.user.OAuth2User

class OAuthUserPrincipal(
    private var authorities: Collection<GrantedAuthority>,
    var socialUser: SocialUser,
) : OAuth2User, UserDetails {

    constructor(provider: OAuthProvider, user: OAuth2User) :
            this(
                authorities = user.authorities,
                socialUser = OAuthUserAttributes(provider, user, UserRole.USER).socialUser,
            )

    constructor(provider: OAuthProvider, userMap: Map<String, Any>) :
            this(
                authorities = listOf(SimpleGrantedAuthority(UserRole.USER.roleType)),
                socialUser = OAuthUserAttributes(provider, userMap, UserRole.USER).socialUser,
            )

    constructor(socialUser: SocialUser) :
            this(
                authorities = listOf(SimpleGrantedAuthority(socialUser.role!!.roleType)),
                socialUser = socialUser,
            )

    override fun getName(): String {
        return username
    }

    override fun getUsername(): String {
        return socialUser.username!!
    }

    override fun getPassword(): String {
        return ""
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }

    override fun getAttributes(): Map<String, Any> {
        return JsonUtils.convertValue(socialUser, object : TypeReference<Map<String, Any>>() {})
    }

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return authorities
    }

}