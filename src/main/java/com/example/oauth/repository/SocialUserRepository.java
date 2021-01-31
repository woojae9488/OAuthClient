package com.example.oauth.repository;

import com.example.oauth.config.oauth.OAuthProvider;
import com.example.oauth.repository.model.SocialUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SocialUserRepository extends JpaRepository<SocialUser, Long> {

    SocialUser findByProviderAndUsername(OAuthProvider provider, String username);

    boolean existsByProviderAndUsername(OAuthProvider provider, String username);

}
