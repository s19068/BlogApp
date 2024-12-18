package com.example.blogApp.blogApp.controller;

import com.example.blogApp.blogApp.model.RedditPost;
import com.example.blogApp.blogApp.service.RedditService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.result.view.RedirectView;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/reddit")
@RequiredArgsConstructor
public class RedditController {

    private final WebClient webClient;

    @GetMapping("/connect")
    public RedirectView initiateRedditConnection() {
        return new RedirectView("/oauth2/authorization/reddit");
    }



    @GetMapping("/posts")
    @ResponseBody
    public Mono<ResponseEntity<?>> getRedditPosts() {
        return webClient.get()
                .uri("/r/askreddit/top?limit=10")
                .retrieve()
                .bodyToMono(String.class)
                .map(ResponseEntity::ok);
    }
}
