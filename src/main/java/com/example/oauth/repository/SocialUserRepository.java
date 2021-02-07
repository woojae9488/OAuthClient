package com.example.oauth.repository;

import com.example.oauth.model.oauth.OAuthProvider;
import com.example.oauth.repository.model.SocialUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SocialUserRepository extends JpaRepository<SocialUser, Long> {

    Optional<SocialUser> findByProviderAndEmail(OAuthProvider provider, String email);

}
