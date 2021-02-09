package com.example.oauth.model.oauth

import org.springframework.social.oauth1.OAuthToken

class OAuth1Requirement(
    var requestToken: OAuthToken = OAuthToken("", ""),
    var authenticationUri: String = "",
)