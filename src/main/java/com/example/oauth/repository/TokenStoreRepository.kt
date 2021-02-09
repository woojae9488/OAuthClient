package com.example.oauth.repository

import com.example.oauth.repository.model.TokenStore
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface TokenStoreRepository : JpaRepository<TokenStore, Long> {

    fun findByUserId(userId: Long): TokenStore?

}