package com.example.oauth.model.oauth

import com.example.oauth.model.oauth.OAuthProvider
import com.example.oauth.model.UserRole
import org.springframework.security.oauth2.core.user.OAuth2User
import com.example.oauth.repository.model.SocialUser
import com.example.oauth.util.JsonUtils
import com.fasterxml.jackson.core.type.TypeReference

class OAuthUserAttributes(
    private var provider: OAuthProvider,
    private var role: UserRole,
    private var providerUserId: Long = 0,
    private var username: String = "",
    private var email: String = "",
    private var profileImage: String = "",
) {

    constructor(provider: OAuthProvider, user: OAuth2User, role: UserRole) : this(provider, role) {
        when (provider) {
            OAuthProvider.KAKAO -> initKakaoOAuthUserAttributes(user)
            OAuthProvider.NAVER -> initNaverOAuthUserAttributes(user)
            else -> {
            }
        }
    }

    constructor(provider: OAuthProvider, userMap: Map<String, Any>, role: UserRole) : this(provider, role) {
        when (provider) {
            OAuthProvider.TWITTER -> initTwitterOAuthUserAttributes(userMap)
            else -> {
            }
        }
    }

    val socialUser: SocialUser
        get() = SocialUser(
            provider = provider,
            providerUserId = providerUserId,
            username = username,
            email = email,
            profileImage = profileImage,
            role = role,
            createTime = System.currentTimeMillis(),
        )

    private fun initKakaoOAuthUserAttributes(user: OAuth2User) {
        val kakaoAccount = user.getAttribute<Map<String, Any>>("kakao_account")!!
        val providerUserIdInt = user.getAttribute<Int>("id")!!
        val profile = kakaoAccount["profile"] as Map<*, *>
        providerUserId = providerUserIdInt.toLong()
        username = profile["nickname"] as String
        email = kakaoAccount["email"] as String
        profileImage = profile["profile_image"] as? String ?: ""
    }

    private fun initNaverOAuthUserAttributes(user: OAuth2User) {
        val response = user.getAttribute<Map<String, Any>>("response")!!
        providerUserId = (response["id"] as String).toLong()
        username = response["name"] as String
        email = response["email"] as String
        profileImage = response["profile_image"] as String
    }

    private fun initTwitterOAuthUserAttributes(userMap: Map<String, Any>) {
        providerUserId = userMap["id"] as Long
        username = userMap["name"] as String
        email = userMap["email"] as String
        profileImage = userMap["profile_image_url"] as String
    }

}