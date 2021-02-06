package com.example.oauth.repository.model;

import com.example.oauth.config.oauth.OAuthProvider;
import com.example.oauth.model.UserRole;
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
                @Index(name = "idx_provider_email", columnList = "provider,email", unique = true)
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
}