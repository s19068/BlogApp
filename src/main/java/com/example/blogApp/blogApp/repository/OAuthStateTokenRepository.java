package com.example.blogApp.blogApp.repository;

import com.example.blogApp.blogApp.model.OAuthStateToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OAuthStateTokenRepository extends JpaRepository<OAuthStateToken, Long> {
    Optional<OAuthStateToken> findByState(String token);
    void deleteByState(String state);
}
