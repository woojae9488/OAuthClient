package com.kwj.oauth.business.user.domain;

import com.kwj.oauth.business.security.model.OAuthProvider;
import com.kwj.oauth.business.security.model.UserRole;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(
        name = "social_user",
        indexes = {
                @Index(name = "idx_provider_n_provider_user_id", columnList = "provider,provider_user_id", unique = true)
        }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SocialUser {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "provider")
    @Enumerated(EnumType.STRING)
    private OAuthProvider provider;

    @Column(name = "provider_user_id")
    private Long providerUserId;

    @Column(name = "username")
    private String username;

    @Column(name = "email")
    private String email;

    @Column(name = "profile_image")
    private String profileImage;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(name = "create_time")
    private Long createTime;

    @Column(name = "update_time")
    private Long updateTime;

}