package com.example.oauth.service

import com.example.oauth.config.properties.AuthorizationTokenProperties
import com.example.oauth.model.token.TokenAttributes
import com.example.oauth.model.token.TokenType
import com.example.oauth.repository.SocialUserRepository
import com.example.oauth.repository.TokenStoreRepository
import com.example.oauth.repository.model.SocialUser
import com.example.oauth.repository.model.TokenStore
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.stereotype.Service
import java.util.*
import javax.mail.AuthenticationFailedException

@Service
class AuthorizationTokenService(
    private val tokenProperties: AuthorizationTokenProperties,
    private val tokenStoreRepository: TokenStoreRepository,
    private val userRepository: SocialUserRepository,
) {

    companion object {
        private const val JWT_SUBJECT_PREFIX = "USER/"
    }

    fun createAccessToken(socialUser: SocialUser): String {
        return createToken(TokenType.ACCESS_TOKEN, socialUser)
    }

    fun createRefreshToken(socialUser: SocialUser, accessToken: String): String {
        val refreshToken = createToken(TokenType.REFRESH_TOKEN, socialUser)
        val existTokenStoreId = tokenStoreRepository.findByUserId(socialUser.id!!)?.id
        val tokenStore = TokenStore(
            id = existTokenStoreId,
            userId = socialUser.id!!,
            accessToken = accessToken,
            refreshToken = refreshToken,
            createTime = System.currentTimeMillis(),
        )

        tokenStoreRepository.save(tokenStore)
        return refreshToken
    }

    private fun createToken(tokenType: TokenType, socialUser: SocialUser): String {
        val now = Date()
        val expiration = Date(now.time + tokenProperties.tokenExpirationMsec(tokenType))
        val tokenAttributes = TokenAttributes.extract(tokenType, socialUser)
        return Jwts.builder()
            .setSubject(JWT_SUBJECT_PREFIX + socialUser.id)
            .setIssuedAt(now)
            .setExpiration(expiration)
            .setClaims(tokenAttributes.attributesMap)
            .signWith(SignatureAlgorithm.HS256, tokenProperties.tokenSecret(tokenType))
            .compact()
    }

    @Throws(AuthenticationFailedException::class)
    fun getPseudoSocialUserFromToken(tokenType: TokenType, token: String): SocialUser {
        return try {
            val claims = Jwts.parser()
                .setSigningKey(tokenProperties.tokenSecret(tokenType))
                .parseClaimsJws(token)
                .body

            val tokenAttributes = TokenAttributes.restore(tokenType, claims)
            tokenAttributes.pseudoSocialUser
        } catch (e: Exception) {
            throw AuthenticationFailedException(e.message)
        }
    }

    fun validateToken(tokenType: TokenType, token: String): Boolean {
        return try {
            Jwts.parser()
                .setSigningKey(tokenProperties.tokenSecret(tokenType))
                .parseClaimsJws(token)

            true
        } catch (e: Exception) {
            false
        }
    }

    @Throws(AuthenticationFailedException::class)
    fun refreshAccessToken(expiredAccessToken: String, refreshToken: String): String {
        val pseudoSocialUser = getPseudoSocialUserFromToken(TokenType.REFRESH_TOKEN, refreshToken)
        val tokenStore = tokenStoreRepository.findByUserId(pseudoSocialUser.id!!)

        return if (validateRefreshToken(tokenStore!!, expiredAccessToken, refreshToken)) {
            val socialUser = userRepository.findById(pseudoSocialUser.id!!)
                .orElseThrow { AuthenticationFailedException("Fail to find social user : ${pseudoSocialUser.id}") }
            val accessToken = createToken(TokenType.ACCESS_TOKEN, socialUser)
            reflectNewAccessTokenToTokenStore(tokenStore, accessToken)

            accessToken
        } else {
            throw AuthenticationFailedException("Fail to validate refresh token : $tokenStore")
        }
    }

    private fun validateRefreshToken(
        tokenStore: TokenStore, expiredAccessToken: String, refreshToken: String
    ): Boolean {
        return expiredAccessToken == tokenStore.accessToken && refreshToken == tokenStore.refreshToken
    }

    private fun reflectNewAccessTokenToTokenStore(tokenStore: TokenStore, accessToken: String) {
        tokenStore.let {
            it.accessToken = accessToken
            it.updateTime = System.currentTimeMillis()
            tokenStoreRepository.save(it)
        }
    }

}