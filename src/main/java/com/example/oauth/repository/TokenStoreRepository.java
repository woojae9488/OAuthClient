package com.example.oauth.repository;

import com.example.oauth.repository.model.TokenStore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenStoreRepository extends JpaRepository<TokenStore, Long> {

    Optional<TokenStore> findByUserId(Long userId);

}
