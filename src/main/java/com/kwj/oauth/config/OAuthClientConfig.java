package com.kwj.oauth.config;

import com.kwj.oauth.business.security.model.OAuthProvider;
import com.kwj.oauth.config.properties.OAuth1ClientProperties;
import com.kwj.oauth.config.properties.OAuth2ClientProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.social.oauth1.OAuth1Operations;
import org.springframework.social.twitter.connect.TwitterConnectionFactory;

import java.util.Arrays;
import java.util.List;

@Configuration
@PropertySource("classpath:oauth-client.properties")
public class OAuthClientConfig {

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        List<ClientRegistration> clientRegistrations = Arrays.asList(
                kakaoClientProperties().generateRegistration(),
                naverClientProperties().generateRegistration()
        );

        return new InMemoryClientRegistrationRepository(clientRegistrations);
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.security.oauth2.client.kakao")
    public OAuth2ClientProperties kakaoClientProperties() {
        return new OAuth2ClientProperties(OAuthProvider.KAKAO);
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.security.oauth2.client.naver")
    public OAuth2ClientProperties naverClientProperties() {
        return new OAuth2ClientProperties(OAuthProvider.NAVER);
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.security.oauth1.client.twitter")
    public OAuth1ClientProperties twitterClientProperties() {
        return new OAuth1ClientProperties(OAuthProvider.TWITTER);
    }

    @Bean
    public OAuth1Operations twitterOAuthOperations() {
        OAuth1ClientProperties twitterClientProperties = twitterClientProperties();
        return new TwitterConnectionFactory(
                twitterClientProperties.getClientId(),
                twitterClientProperties.getClientSecret()
        ).getOAuthOperations();
    }

}