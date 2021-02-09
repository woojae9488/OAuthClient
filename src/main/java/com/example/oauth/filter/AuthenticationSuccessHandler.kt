package com.example.oauth.filter

import com.example.oauth.repository.SocialUserRepository
import com.example.oauth.service.AuthorizationTokenService
import com.example.oauth.config.properties.AuthorizationTokenProperties
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import kotlin.Throws
import java.io.IOException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import com.example.oauth.model.oauth.OAuthUserPrincipal
import com.example.oauth.model.token.TokenType
import com.example.oauth.repository.model.SocialUser
import com.example.oauth.util.CookieUtils
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component

@Component
class AuthenticationSuccessHandler(
    private val userRepository: SocialUserRepository,
    private val tokenService: AuthorizationTokenService,
    private val tokenProperties: AuthorizationTokenProperties,
) : SimpleUrlAuthenticationSuccessHandler() {

    companion object {
        const val OAUTH_SUCCESS_REDIRECT_URL = "/"
    }

    @Throws(IOException::class)
    override fun onAuthenticationSuccess(
        request: HttpServletRequest, response: HttpServletResponse, authentication: Authentication
    ) {
        if (response.isCommitted) return

        val userPrincipal = authentication.principal as OAuthUserPrincipal
        val socialUser = userPrincipal.socialUser
        val socialUserEntity = getSocialUserEntity(socialUser)
        socialUserEntity?.also { userPrincipal.socialUser = it } ?: userRepository.save(socialUser)

        setAuthorizationTokenCookies(response, userPrincipal.socialUser)
        redirectStrategy.sendRedirect(request, response, OAUTH_SUCCESS_REDIRECT_URL)
    }

    private fun getSocialUserEntity(socialUser: SocialUser): SocialUser? {
        return userRepository.findByProviderAndProviderUserId(socialUser.provider!!, socialUser.providerUserId!!)
    }

    private fun setAuthorizationTokenCookies(response: HttpServletResponse, socialUser: SocialUser) {
        val accessToken = tokenService.createAccessToken(socialUser)
        val refreshToken = tokenService.createRefreshToken(socialUser, accessToken)
        saveTokenToCookie(response, TokenType.ACCESS_TOKEN, accessToken)
        saveTokenToCookie(response, TokenType.REFRESH_TOKEN, refreshToken)
    }

    private fun saveTokenToCookie(response: HttpServletResponse, tokenType: TokenType, token: String) {
        val cookieKey = tokenProperties.tokenCookieKey(tokenType)
        val cookieMaxAge = tokenProperties.tokenCookieMaxAge(tokenType)
        CookieUtils.setCookie(response, cookieKey, token, cookieMaxAge)
    }

}