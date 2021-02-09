package com.example.oauth.model.token

import com.example.oauth.model.UserRole
import com.example.oauth.model.oauth.OAuthProvider
import com.example.oauth.repository.model.SocialUser
import io.jsonwebtoken.Claims

class AccessTokenAttributes(
    private var id: Long,
    private var provider: OAuthProvider,
    private var username: String,
    private var profileImage: String,
    private var role: UserRole,
) : TokenAttributes(TokenType.ACCESS_TOKEN) {

    override val pseudoSocialUser = SocialUser(
        id = id,
        provider = provider,
        username = username,
        profileImage = profileImage,
        role = role,
    )

    companion object {
        fun of(socialUser: SocialUser): AccessTokenAttributes {
            return AccessTokenAttributes(
                id = socialUser.id!!,
                provider = socialUser.provider!!,
                username = socialUser.username!!,
                profileImage = socialUser.profileImage!!,
                role = socialUser.role!!,
            )
        }

        fun of(claims: Claims): AccessTokenAttributes {
            return AccessTokenAttributes(
                id = claims.get("id", Number::class.java).toLong(),
                provider = OAuthProvider.valueOf(claims.get("provider", String::class.java)),
                username = claims.get("username", String::class.java),
                profileImage = claims.get("profileImage", String::class.java),
                role = UserRole.valueOf(claims.get("role", String::class.java)),
            )
        }
    }

}