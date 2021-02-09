package com.example.oauth.config.properties

import com.example.oauth.model.token.TokenType
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class AuthorizationTokenProperties(
    private val accessTokenCookieKey: String = "access-token",
    private val refreshTokenCookieKey: String = "refresh-token",

    @Value("\${security.token.access.secret}")
    private val accessTokenSecret: String,

    @Value("\${security.token.access.expirationMsec}")
    private val accessTokenExpirationMsec: Long,

    @Value("\${security.token.refresh.secret}")
    private val refreshTokenSecret: String,

    @Value("\${security.token.refresh.expirationMsec}")
    private var refreshTokenExpirationMsec: Long,
) {

    fun tokenSecret(tokenType: TokenType): String {
        return when (tokenType) {
            TokenType.ACCESS_TOKEN -> accessTokenSecret
            TokenType.REFRESH_TOKEN -> refreshTokenSecret
        }
    }

    fun tokenExpirationMsec(tokenType: TokenType): Long {
        return when (tokenType) {
            TokenType.ACCESS_TOKEN -> accessTokenExpirationMsec
            TokenType.REFRESH_TOKEN -> refreshTokenExpirationMsec
        }
    }

    fun tokenCookieKey(tokenType: TokenType): String {
        return when (tokenType) {
            TokenType.ACCESS_TOKEN -> accessTokenCookieKey
            TokenType.REFRESH_TOKEN -> refreshTokenCookieKey
        }
    }

    fun tokenCookieMaxAge(tokenType: TokenType): Int {
        return when (tokenType) {
            TokenType.ACCESS_TOKEN -> (accessTokenExpirationMsec / 1000).toInt()
            TokenType.REFRESH_TOKEN -> (refreshTokenExpirationMsec / 1000).toInt()
        }
    }

}