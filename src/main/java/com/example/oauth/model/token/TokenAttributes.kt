package com.example.oauth.model.token

import com.example.oauth.repository.model.SocialUser
import com.example.oauth.util.JsonUtils
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.core.type.TypeReference
import io.jsonwebtoken.Claims

@JsonIgnoreProperties(ignoreUnknown = true)
abstract class TokenAttributes protected constructor(private val type: TokenType) {

    val attributesMap: Map<String, Any>
        get() = JsonUtils.convertValue(pseudoSocialUser, object : TypeReference<Map<String, Any>>() {})

    abstract val pseudoSocialUser: SocialUser

    companion object {
        fun extract(tokenType: TokenType, socialUser: SocialUser): TokenAttributes {
            return when (tokenType) {
                TokenType.ACCESS_TOKEN -> AccessTokenAttributes.of(socialUser)
                TokenType.REFRESH_TOKEN -> RefreshTokenAttributes.of(socialUser)
            }
        }

        fun restore(tokenType: TokenType, claims: Claims): TokenAttributes {
            return when (tokenType) {
                TokenType.ACCESS_TOKEN -> AccessTokenAttributes.of(claims)
                TokenType.REFRESH_TOKEN -> RefreshTokenAttributes.of(claims)
            }
        }
    }

}