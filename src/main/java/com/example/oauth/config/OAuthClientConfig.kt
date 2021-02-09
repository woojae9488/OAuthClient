package com.example.oauth.config

import com.example.oauth.config.properties.OAuth1ClientProperties
import com.example.oauth.config.properties.OAuth2ClientProperties
import com.example.oauth.model.oauth.OAuthProvider
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository
import org.springframework.social.oauth1.OAuth1Operations
import org.springframework.social.twitter.connect.TwitterConnectionFactory
import java.util.*

@Configuration
@PropertySource("classpath:oauth-client.properties")
class OAuthClientConfig {

    @Bean
    fun clientRegistrationRepository(): ClientRegistrationRepository {
        val clientRegistrations = ArrayList<ClientRegistration>()
        clientRegistrations.add(kakaoClientProperties().generateRegistration())
        clientRegistrations.add(naverClientProperties().generateRegistration())
        return InMemoryClientRegistrationRepository(clientRegistrations)
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.security.oauth2.client.kakao")
    fun kakaoClientProperties(): OAuth2ClientProperties {
        return OAuth2ClientProperties(OAuthProvider.KAKAO)
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.security.oauth2.client.naver")
    fun naverClientProperties(): OAuth2ClientProperties {
        return OAuth2ClientProperties(OAuthProvider.NAVER)
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.security.oauth1.client.twitter")
    fun twitterClientProperties(): OAuth1ClientProperties {
        return OAuth1ClientProperties(OAuthProvider.TWITTER)
    }

    @Bean
    fun twitterOAuthOperations(): OAuth1Operations {
        val twitterClientProperties = twitterClientProperties()
        return TwitterConnectionFactory(
            twitterClientProperties.clientId,
            twitterClientProperties.clientSecret
        ).oAuthOperations
    }

}