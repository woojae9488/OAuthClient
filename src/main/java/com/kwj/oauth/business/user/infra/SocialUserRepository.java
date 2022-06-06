package com.kwj.oauth.business.user.infra;

import com.kwj.oauth.business.security.model.OAuthProvider;
import com.kwj.oauth.business.user.domain.SocialUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SocialUserRepository extends JpaRepository<SocialUser, Long> {

    Optional<SocialUser> findByProviderAndProviderUserId(OAuthProvider provider, Long providerUserId);

}
