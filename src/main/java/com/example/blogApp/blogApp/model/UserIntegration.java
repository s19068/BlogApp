package com.example.blogApp.blogApp.model;

import com.example.blogApp.blogApp.model.security.OAuth2AuthorizedClientEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "user_integrations")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserIntegration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = false)
    private String providerName;

    @Column(nullable = false)
    private String clientId;

    @Column(nullable = false)
    private String clientSecret;

    @Column(nullable = false)
    private String redirectUri; // Dynamiczny redirect URI

    @Column(nullable = false)
    private String authorizationUri; // URL autoryzacji

    @Column(nullable = false)
    private String tokenUri; // URL wymiany tokenów

    @Column(nullable = true)
    private String userInfoUri; // URL do pobrania informacji o użytkowniku (opcjonalne)

    @Column(nullable = true)
    private String userNameAttribute; // Atrybut identyfikujący użytkownika (np. "name", "email")

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_integration_scopes", joinColumns = @JoinColumn(name = "integration_id"))
    @Column(name = "scope")
    private List<String> scopes; // Zakresy OAuth2

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "integration", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OAuth2AuthorizedClientEntity> authorizedClients;
}
