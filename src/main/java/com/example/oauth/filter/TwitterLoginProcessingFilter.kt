package com.example.oauth.filter

import com.example.oauth.config.properties.OAuth1ClientProperties
import org.springframework.social.oauth1.OAuth1Operations
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import com.example.oauth.filter.TwitterLoginProcessingFilter
import kotlin.Throws
import javax.servlet.ServletException
import java.io.IOException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.FilterChain
import com.example.oauth.model.oauth.OAuth1Requirement
import org.springframework.security.web.util.matcher.RequestMatcher
import org.springframework.social.oauth1.OAuthToken
import org.springframework.social.oauth1.OAuth1Parameters
import org.springframework.stereotype.Component

@Component
class TwitterLoginProcessingFilter(
    private val clientProperties: OAuth1ClientProperties,
    private val oauthOperations: OAuth1Operations,
) : OncePerRequestFilter() {

    companion object {
        private const val LOGIN_REQUEST_URL = "/oauth1/authorization/twitter"
    }

    private val requireLoginRequestMatcher = AntPathRequestMatcher(LOGIN_REQUEST_URL)

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        if (requiresLoginProcessing(request)) {
            val oauth1Requirement = twitterOAuthRequirement
            request.servletContext.setAttribute("token", oauth1Requirement.requestToken)
            response.sendRedirect(oauth1Requirement.authenticationUri)
            return
        }
        chain.doFilter(request, response)
    }

    private fun requiresLoginProcessing(request: HttpServletRequest): Boolean {
        return requireLoginRequestMatcher.matches(request)
    }

    private val twitterOAuthRequirement: OAuth1Requirement
        get() {
            val requestToken = oauthOperations.fetchRequestToken(clientProperties.callbackUri, null)
            val authenticationUri = oauthOperations.buildAuthenticateUrl(requestToken.value, OAuth1Parameters())
            return OAuth1Requirement(requestToken, authenticationUri)
        }

}