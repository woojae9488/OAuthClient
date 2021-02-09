package com.example.oauth.config.properties

import com.example.oauth.model.oauth.OAuthProvider

class OAuth1ClientProperties(
    val provider: OAuthProvider,
    var clientId: String = "",
    var clientSecret: String = "",
    var callbackUri: String = "",
    var userInfoUri: String = "",
    var userNameAttribute: String = "",
)