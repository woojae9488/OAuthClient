package com.example.oauth.model.oauth

enum class OAuthProvider(val id: String) {
    KAKAO("kakao"), NAVER("naver"), TWITTER("twitter");

    companion object {
        fun idOf(id: String): OAuthProvider? {
            for (provider in values()) {
                if (id == provider.id) {
                    return provider
                }
            }
            return null
        }
    }

}