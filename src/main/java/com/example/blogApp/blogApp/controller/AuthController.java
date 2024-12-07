package com.example.blogApp.blogApp.controller;

import com.example.blogApp.blogApp.exceptions.HttpException;
import com.example.blogApp.blogApp.model.dto.request.AuthenticationRequest;
import com.example.blogApp.blogApp.model.dto.request.RegisterRequest;
import com.example.blogApp.blogApp.model.dto.response.AuthenticationResponse;
import com.example.blogApp.blogApp.model.dto.response.JwtResponse;
import com.example.blogApp.blogApp.model.dto.response.ServerResponse;
import com.example.blogApp.blogApp.security.jwt.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody AuthenticationRequest authenticationRequest) {
        JwtResponse jwt = authService.login(authenticationRequest);
        return ResponseEntity.ok(jwt);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest) {
        try {
            authService.registerUser(registerRequest);
        } catch (HttpException e) {
            return new ResponseEntity<>(e.getMessage(), e.getStatusCode());
        }

        return ResponseEntity.ok(new ServerResponse("User registered successfully!", HttpStatus.OK));
    }


}
