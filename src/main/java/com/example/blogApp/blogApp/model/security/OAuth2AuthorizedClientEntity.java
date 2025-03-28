package com.example.blogApp.blogApp.model.security;

import com.example.blogApp.blogApp.model.User;
import com.example.blogApp.blogApp.model.UserIntegration;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OAuth2AuthorizedClientEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String accessToken;
    private String refreshToken;
    private Instant accessTokenExpiresAt;
    private Instant accessTokenIssuedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "integration_id", nullable = false)
    private UserIntegration integration;
}
