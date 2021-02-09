package com.example.oauth.repository.model

import au.com.console.kassava.kotlinEquals
import au.com.console.kassava.kotlinHashCode
import au.com.console.kassava.kotlinToString
import javax.persistence.*

@Entity
@Table(name = "token_store", indexes = [Index(name = "idx_user_id", columnList = "user_id", unique = true)])
class TokenStore(
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,

    @Column(name = "user_id")
    var userId: Long = 0,

    @Column(name = "access_token", length = 500)
    var accessToken: String = "",

    @Column(name = "refresh_token")
    var refreshToken: String = "",

    @Column(name = "create_time")
    var createTime: Long = 0,

    @Column(name = "update_time")
    var updateTime: Long = 0,
) {

    override fun toString() = kotlinToString(properties = toStringProperties)

    override fun equals(other: Any?) = kotlinEquals(other = other, properties = equalsAndHashCodeProperties)

    override fun hashCode() = kotlinHashCode(properties = equalsAndHashCodeProperties)

    companion object {
        private val equalsAndHashCodeProperties = arrayOf(TokenStore::id)
        private val toStringProperties = arrayOf(
            TokenStore::id,
            TokenStore::userId,
            TokenStore::accessToken,
            TokenStore::refreshToken,
            TokenStore::createTime,
            TokenStore::updateTime,
        )
    }

}