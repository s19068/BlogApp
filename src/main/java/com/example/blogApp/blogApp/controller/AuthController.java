package com.example.blogApp.blogApp.controller;

import com.example.blogApp.blogApp.exceptions.HttpException;
import com.example.blogApp.blogApp.model.dto.request.AuthenticationRequest;
import com.example.blogApp.blogApp.model.dto.request.RegisterRequest;
import com.example.blogApp.blogApp.model.dto.response.JwtResponse;
import com.example.blogApp.blogApp.model.dto.response.ServerResponse;
import com.example.blogApp.blogApp.security.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
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
    public ResponseEntity<?> authenticateUser(@RequestBody AuthenticationRequest authenticationRequest,
                                              HttpServletResponse response) {
        JwtResponse jwt = authService.login(authenticationRequest);

        ResponseCookie cookie = ResponseCookie.from("token", jwt.getToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(24 * 60 * 60)
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok("zalogowano pomyślnie");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("token", "")
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.ok("Wylogowano");
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
