package com.example.blogApp.blogApp.service;

import com.example.blogApp.blogApp.model.User;
import com.example.blogApp.blogApp.model.UserIntegration;
import com.example.blogApp.blogApp.repository.UserIntegrationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserIntegrationService {

    private final UserIntegrationRepository integrationRepository;

    @Transactional
    public UserIntegration saveOrUpdateIntegration(User user, String providerName, String clientId, String clientSecret) {
        Optional<UserIntegration> existingIntegration = integrationRepository.findByUser_UserIdAndProviderName(
                user.getUserId(), providerName);

        UserIntegration integration = existingIntegration.orElseGet(() -> UserIntegration.builder()
                .user(user)
                .providerName(providerName)
                .build());

        integration.setClientId(clientId);
        integration.setClientSecret(clientSecret);

        return integrationRepository.save(integration);
    }

    public List<UserIntegration> getUserIntegrations(Long userId) {
        return integrationRepository.findByUser_UserId(userId);
    }

    @Transactional
    public void deleteIntegration(User user, String providerName) {
        integrationRepository.findByUser_UserIdAndProviderName(user.getUserId(), providerName)
                .ifPresent(integrationRepository::delete);
    }

    public Optional<UserIntegration> getIntegrationByUserAndProvider(Long userId, String providerName) {
        return integrationRepository.findByUser_UserIdAndProviderName(userId, providerName);
    }
}
