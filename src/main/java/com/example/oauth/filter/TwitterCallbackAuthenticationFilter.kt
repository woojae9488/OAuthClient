package com.example.oauth.filter

import com.example.oauth.config.properties.OAuth1ClientProperties
import com.example.oauth.model.oauth.OAuthProvider
import com.example.oauth.model.oauth.OAuthUserPrincipal
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import org.springframework.security.web.authentication.WebAuthenticationDetails
import org.springframework.social.oauth1.AuthorizedRequestToken
import org.springframework.social.oauth1.OAuth1Operations
import org.springframework.social.oauth1.OAuthToken
import org.springframework.social.twitter.api.impl.TwitterTemplate
import org.springframework.stereotype.Component
import org.springframework.web.client.getForObject
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class TwitterCallbackAuthenticationFilter(
    private val clientProperties: OAuth1ClientProperties,
    private val oauthOperations: OAuth1Operations,
) : AbstractAuthenticationProcessingFilter(CALLBACK_PROCESSING_URL) {

    companion object {
        private const val CALLBACK_PROCESSING_URL = "/login/oauth1/twitter/callback"
    }

    @Autowired
    override fun setAuthenticationManager(authenticationManager: AuthenticationManager) {
        super.setAuthenticationManager(authenticationManager)
    }

    @Throws(AuthenticationException::class)
    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication {
        val requestToken = request.servletContext.getAttribute("token") as OAuthToken
        request.servletContext.removeAttribute("token")
        val oauthVerifier = request.getParameter("oauth_verifier")
        val twitterTemplate = generateTwitterTemplate(requestToken, oauthVerifier)
        val userPrincipal = generateOAuthUserPrincipal(twitterTemplate)
        return generateAuthentication(request, userPrincipal)
    }

    private fun generateTwitterTemplate(requestToken: OAuthToken, oauthVerifier: String): TwitterTemplate {
        val authorizedRequestToken = AuthorizedRequestToken(requestToken, oauthVerifier)
        val accessToken = oauthOperations.exchangeForAccessToken(authorizedRequestToken, null)
        return TwitterTemplate(
            clientProperties.clientId, clientProperties.clientSecret,
            accessToken.value, accessToken.secret
        )
    }

    fun generateOAuthUserPrincipal(twitterTemplate: TwitterTemplate): OAuthUserPrincipal {
        val userMap = twitterTemplate.restOperations()
            .getForObject(clientProperties.userInfoUri, Map::class) as Map<String, Any>
        return OAuthUserPrincipal(OAuthProvider.TWITTER, userMap)
    }

    private fun generateAuthentication(request: HttpServletRequest, userPrincipal: OAuthUserPrincipal): Authentication {
        return UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.authorities).apply {
            details = WebAuthenticationDetails(request)
        }
    }

}