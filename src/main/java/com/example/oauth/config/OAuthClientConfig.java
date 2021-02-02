package com.example.oauth.config;

import com.example.oauth.config.oauth.OAuth1ClientProperties;
import com.example.oauth.config.oauth.OAuth2ClientProperties;
import com.example.oauth.config.oauth.OAuthProvider;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.social.oauth1.OAuth1Operations;
import org.springframework.social.twitter.connect.TwitterConnectionFactory;

import java.util.ArrayList;
import java.util.List;

@Configuration
@PropertySource("classpath:oauth-client.properties")
public class OAuthClientConfig {

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        List<ClientRegistration> clientRegistrations = new ArrayList<>();
        clientRegistrations.add(kakaoClientProperties().generateRegistration());
        clientRegistrations.add(naverClientProperties().generateRegistration());
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
