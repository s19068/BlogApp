package com.example.blogApp.blogApp.repository;

import com.example.blogApp.blogApp.model.UserIntegration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserIntegrationRepository extends JpaRepository<UserIntegration, Long> {
    List<UserIntegration> findByUser_UserId(Long userId);

    Optional<UserIntegration> findByUser_UserIdAndProviderName(Long userId, String providerName);

}
