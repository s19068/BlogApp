package com.example.blogApp.blogApp.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/user-info")
    public String getUserInfo(@AuthenticationPrincipal OAuth2AuthenticationToken token) {
        return "Logged in as: " + token.getPrincipal().getAttributes();
    }
}
