package com.example.blogApp.blogApp.model.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.Instant;

@Data
public class TokenResponse {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("expires_in")
    private long expiresIn;

    @JsonProperty("scope")
    private String scope;

    // Pomocnicze metody do obsługi czasu ważności tokena
    public Instant getIssuedAt() {
        return Instant.now();
    }

    public Instant getExpiresAt() {
        return Instant.now().plusSeconds(expiresIn);
    }
}
