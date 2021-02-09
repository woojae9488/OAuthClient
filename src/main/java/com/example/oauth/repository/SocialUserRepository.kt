package com.example.oauth.repository

import com.example.oauth.model.oauth.OAuthProvider
import com.example.oauth.repository.model.SocialUser
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SocialUserRepository : JpaRepository<SocialUser, Long> {

    fun findByProviderAndProviderUserId(provider: OAuthProvider, providerUserId: Long): SocialUser?

}