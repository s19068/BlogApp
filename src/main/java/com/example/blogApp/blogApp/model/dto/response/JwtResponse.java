package com.example.blogApp.blogApp.model.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
@Builder
public class JwtResponse {
    String token;
    String email;
    String type;
    Long userId;
    Collection<String> roles;
}
