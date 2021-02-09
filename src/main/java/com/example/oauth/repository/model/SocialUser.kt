package com.example.oauth.repository.model

import au.com.console.kassava.kotlinEquals
import au.com.console.kassava.kotlinHashCode
import au.com.console.kassava.kotlinToString
import com.example.oauth.model.UserRole
import com.example.oauth.model.oauth.OAuthProvider
import com.fasterxml.jackson.annotation.JsonInclude
import javax.persistence.*

@Entity
@Table(
    name = "social_user",
    indexes = [Index(name = "idx_provider_n_provider_user_id", columnList = "provider,provider_user_id", unique = true)]
)
@JsonInclude(JsonInclude.Include.NON_NULL)
class SocialUser(
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "provider")
    @Enumerated(EnumType.STRING)
    var provider: OAuthProvider? = null,

    @Column(name = "provider_user_id")
    var providerUserId: Long? = null,

    @Column(name = "username")
    var username: String? = null,

    @Column(name = "email")
    var email: String? = null,

    @Column(name = "profile_image")
    var profileImage: String? = null,

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    var role: UserRole? = null,

    @Column(name = "create_time")
    var createTime: Long? = null,

    @Column(name = "update_time")
    var updateTime: Long? = null,
) {

    override fun toString() = kotlinToString(properties = toStringProperties)

    override fun equals(other: Any?) = kotlinEquals(other = other, properties = equalsAndHashCodeProperties)

    override fun hashCode() = kotlinHashCode(properties = equalsAndHashCodeProperties)

    companion object {
        private val equalsAndHashCodeProperties = arrayOf(SocialUser::id)
        private val toStringProperties = arrayOf(
            SocialUser::id,
            SocialUser::provider,
            SocialUser::providerUserId,
            SocialUser::username,
            SocialUser::email,
            SocialUser::profileImage,
            SocialUser::role,
            SocialUser::createTime,
            SocialUser::updateTime,
        )
    }

}