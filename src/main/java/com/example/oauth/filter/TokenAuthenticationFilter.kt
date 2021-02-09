package com.example.oauth.filter

import com.example.oauth.config.properties.AuthorizationTokenProperties
import com.example.oauth.model.oauth.OAuthUserPrincipal
import com.example.oauth.model.token.TokenType
import com.example.oauth.repository.model.SocialUser
import com.example.oauth.service.AuthorizationTokenService
import com.example.oauth.util.CookieUtils
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetails
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException
import java.util.function.Supplier
import javax.mail.AuthenticationFailedException
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class TokenAuthenticationFilter(
    private val tokenService: AuthorizationTokenService,
    private val tokenProperties: AuthorizationTokenProperties,
) : OncePerRequestFilter() {

    @Throws(ServletException::class, IOException::class, AuthenticationFailedException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val accessToken = getTokenFromRequest(TokenType.ACCESS_TOKEN, request)
        accessToken ?: return filterChain.doFilter(request, response)
        val refreshToken = getTokenFromRequest(TokenType.REFRESH_TOKEN, request)
        refreshToken ?: return filterChain.doFilter(request, response)

        val authentication = attemptAuthentication(request, accessToken)
        authentication?.also {
            SecurityContextHolder.getContext().authentication = it
        } ?: run {
            val refreshedAccessToken = tokenService.refreshAccessToken(accessToken, refreshToken)
            saveRefreshedAccessTokenToCookie(response, refreshedAccessToken)
            val refreshedAuthentication = attemptAuthentication(request, refreshedAccessToken)
            SecurityContextHolder.getContext().authentication = refreshedAuthentication
        }

        filterChain.doFilter(request, response)
    }

    private fun getTokenFromRequest(tokenType: TokenType, request: HttpServletRequest): String? {
        return CookieUtils.getCookie(request, tokenProperties.tokenCookieKey(tokenType))
            ?.let {
                if (StringUtils.hasText(it.value)) it.value else null
            }
    }

    @Throws(AuthenticationFailedException::class)
    private fun attemptAuthentication(request: HttpServletRequest, accessToken: String): Authentication? {
        return if (tokenService.validateToken(TokenType.ACCESS_TOKEN, accessToken)) {
            val pseudoSocialUser = tokenService.getPseudoSocialUserFromToken(TokenType.ACCESS_TOKEN, accessToken)
            val userPrincipal = OAuthUserPrincipal(pseudoSocialUser)

            UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.authorities).apply {
                details = WebAuthenticationDetails(request)
            }
        } else null
    }

    private fun saveRefreshedAccessTokenToCookie(response: HttpServletResponse, refreshedAccessToken: String) {
        val cookieKey = tokenProperties.tokenCookieKey(TokenType.ACCESS_TOKEN)
        val cookieMaxAge = tokenProperties.tokenCookieMaxAge(TokenType.ACCESS_TOKEN)
        CookieUtils.setCookie(response, cookieKey, refreshedAccessToken, cookieMaxAge)
    }

}