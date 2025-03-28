package com.example.blogApp.blogApp.controller;

import com.example.blogApp.blogApp.model.User;
import com.example.blogApp.blogApp.model.UserIntegration;
import com.example.blogApp.blogApp.service.UserIntegrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/integrations")
@RequiredArgsConstructor
public class UserIntegrationController {

    private final UserIntegrationService integrationService;

    @PostMapping
    public ResponseEntity<?> saveOrUpdateIntegration(
            @AuthenticationPrincipal User user,
            @RequestBody UserIntegration request) {
        UserIntegration savedIntegration = integrationService.saveOrUpdateIntegration(
                user, request.getProviderName(), request.getClientId(), request.getClientSecret());
        return ResponseEntity.ok(savedIntegration);
    }

    @GetMapping
    public ResponseEntity<List<UserIntegration>> getIntegrations(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(integrationService.getUserIntegrations(user.getUserId()));
    }

    @DeleteMapping("/{providerName}")
    public ResponseEntity<?> deleteIntegration(
            @AuthenticationPrincipal User user,
            @PathVariable String providerName) {
        integrationService.deleteIntegration(user, providerName);
        return ResponseEntity.ok().build();
    }
}
