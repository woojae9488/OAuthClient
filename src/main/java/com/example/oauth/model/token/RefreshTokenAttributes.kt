package com.example.oauth.model.token

import com.example.oauth.repository.model.SocialUser
import io.jsonwebtoken.Claims

class RefreshTokenAttributes(
    private var id: Long,
) : TokenAttributes(TokenType.REFRESH_TOKEN) {

    override val pseudoSocialUser = SocialUser(
        id = id
    )

    companion object {
        fun of(socialUser: SocialUser): RefreshTokenAttributes {
            return RefreshTokenAttributes(
                id = socialUser.id!!
            )
        }

        fun of(claims: Claims): RefreshTokenAttributes {
            return RefreshTokenAttributes(
                id = claims.get("id", Long::class.java)
            )
        }
    }

}