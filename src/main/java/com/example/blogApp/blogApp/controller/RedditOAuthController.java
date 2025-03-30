package com.example.blogApp.blogApp.controller;

import com.example.blogApp.blogApp.model.OAuthStateToken;
import com.example.blogApp.blogApp.model.RedditAccount;
import com.example.blogApp.blogApp.model.User;
import com.example.blogApp.blogApp.model.dto.response.TokenResponse;
import com.example.blogApp.blogApp.repository.OAuthStateTokenRepository;
import com.example.blogApp.blogApp.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.URLEncoder;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

@RestController
@RequestMapping("/api/reddit")
@RequiredArgsConstructor
public class RedditOAuthController {

    @Value("${reddit.client-id}") private String redditClientId;
    @Value("${reddit.redirect-uri}") private String redditRedirectUri;
    @Value("${reddit.scopes}") private String redditScopes;
    @Value("${reddit.client-secret}") private String redditClientSecret;
    private final UserRepository userRepository;
    private final OAuthStateTokenRepository oAuthStateTokenRepository;

    @GetMapping("/authorize")
    public void authorizeReddit(
            HttpServletResponse response,
            @AuthenticationPrincipal UserDetails userDetails
    ) throws IOException {

        User user = userRepository.findByUserName(userDetails.getUsername());
        if (user == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not found");
            return;
        }


        String state = UUID.randomUUID().toString();
        OAuthStateToken oAuthStateToken = OAuthStateToken.builder()
                .state(state)
                .user(user)
                .build();
        oAuthStateTokenRepository.save(oAuthStateToken);
        System.out.println("🆔 Wygenerowany state: " + state);
        System.out.println("💾 Token zapisany w bazie: " + oAuthStateToken);
        String authUrl = "https://www.reddit.com/api/v1/authorize?client_id=" + redditClientId
                + "&response_type=code"
                + "&state=" + state
                + "&redirect_uri=" + URLEncoder.encode(redditRedirectUri, "UTF-8")
                + "&duration=permanent"
                + "&scope=" + URLEncoder.encode(redditScopes, "UTF-8");


        response.sendRedirect(authUrl);
    }

    /*@GetMapping("/authorize")
    public void authorizeReddit(
            HttpServletResponse response,
            @RequestParam("token") String token
    ) throws IOException {
        // Tu zweryfikuj token ręcznie
        String username = jwtUtils.getUserNameFromJwtToken(token); // zakładam, że masz JwtUtils
        Optional<User> userOptional = Optional.ofNullable(userRepository.findByUserName(username));
        User user = userOptional.orElseThrow(() -> new UsernameNotFoundException(username));

        // Dalej jak było:
        String state = UUID.randomUUID().toString();
        OAuthStateToken oAuthStateToken = OAuthStateToken.builder()
                .state(state)
                .user(user)
                .build();
        oAuthStateTokenRepository.save(oAuthStateToken);


        String authUrl = "https://www.reddit.com/api/v1/authorize?client_id=" + redditClientId
                + "&response_type=code"
                + "&state=" + state
                + "&redirect_uri=" + URLEncoder.encode(redditRedirectUri, "UTF-8")
                + "&duration=permanent"
                + "&scope=" + URLEncoder.encode(redditScopes, "UTF-8");

        response.sendRedirect(authUrl);
    }*/

    /*@GetMapping("/authorize")
    public void authorizeReddit(HttpServletResponse response, @AuthenticationPrincipal UserDetails userDetails) throws IOException {
        // Only allow if user is authenticated (Spring Security ensures this).
        String state = UUID.randomUUID().toString();
        Optional<User> userOptional = Optional.ofNullable(userRepository.findByUserName(userDetails.getUsername()));
        User user = userOptional.orElseThrow(() -> new UsernameNotFoundException(userDetails.getUsername()));
        OAuthStateToken oAuthStateToken = OAuthStateToken.builder()
                .state(state)
                .user(user)
                .build();

        oAuthStateTokenRepository.save(oAuthStateToken);

        String authUrl = "https://www.reddit.com/api/v1/authorize?client_id=" + redditClientId
                + "&response_type=code"
                + "&state=" + state
                + "&redirect_uri=" + URLEncoder.encode(redditRedirectUri, "UTF-8")
                + "&duration=permanent"
                + "&scope=" + URLEncoder.encode(redditScopes, "UTF-8");
        // Redirect user's browser to Reddit OAuth2 authorization page:
        response.sendRedirect(authUrl);
    }*/

    @GetMapping("/callback")
    public void redditCallback(@RequestParam(required=false) String code,
                               @RequestParam(required=false) String state,
                               @RequestParam(required=false) String error,
                               HttpServletResponse response) throws IOException {
        if (error != null) {
            // User denied or there was an error – handle accordingly
            // e.g., redirect to an error page or display a message.
            response.sendRedirect("/oauth-error?error=" + error);
            return;
        }
        if (state == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing state");
            return;
        }

        OAuthStateToken oAuthStateToken = oAuthStateTokenRepository.findByState(state)
                .orElseThrow(() -> new IllegalStateException("Invalid or expired state"));


        // Exchange the authorization code for an access token
        String clientAuth = Base64.getEncoder().encodeToString((redditClientId + ":" + redditClientSecret).getBytes());
        WebClient webClient = WebClient.create("https://www.reddit.com");
        Mono<TokenResponse> tokenResponse = webClient.post()
                .uri("/api/v1/access_token")
                .header(HttpHeaders.AUTHORIZATION, "Basic " + clientAuth)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("grant_type", "authorization_code")
                        .with("code", code)
                        .with("redirect_uri", redditRedirectUri))
                .retrieve()
                .bodyToMono(TokenResponse.class);

        TokenResponse tokens = tokenResponse.block();  // call Reddit (synchronously for simplicity)
        if (tokens == null || tokens.getAccessToken() == null) {
            response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failed to retrieve tokens from Reddit");
            return;
        }
        // Save tokens in DB for this user
        User user = oAuthStateToken.getUser();
        oAuthStateTokenRepository.delete(oAuthStateToken); // usuwamy po wykorzystaniu

// sprawdzamy czy user już ma RedditAccount
        RedditAccount redditAccount = user.getRedditAccount();
        if (redditAccount == null) {
            redditAccount = new RedditAccount();
            redditAccount.setUser(user);
        }

        redditAccount.setAccessToken(tokens.getAccessToken());
        redditAccount.setRefreshToken(tokens.getRefreshToken());
        redditAccount.setTokenExpiry(Instant.now().plusSeconds(tokens.getExpiresIn()));

        user.setRedditAccount(redditAccount); // przypisanie dwukierunkowej relacji
        userRepository.save(user); // zapisuje też RedditAccount dzięki cascade

        // Redirect back to frontend (maybe to a success page)
        response.sendRedirect("http://localhost:5174/dashboard");
    }

    private String getValidAccessToken(User user) {
        RedditAccount redditAccount = user.getRedditAccount();
        if (redditAccount == null || redditAccount.getRefreshToken() == null) {
            throw new IllegalStateException("Brak konta Reddit lub refresh tokena");
        }

        Instant now = Instant.now();
        if (redditAccount.getTokenExpiry() != null && redditAccount.getTokenExpiry().isAfter(now)) {
            // ✅ Token jeszcze ważny
            return redditAccount.getAccessToken();
        }

        // 🔁 Token wygasł – odświeżamy
        String clientAuth = Base64.getEncoder().encodeToString((redditClientId + ":" + redditClientSecret).getBytes());

        WebClient webClient = WebClient.create("https://www.reddit.com");
        Mono<TokenResponse> tokenResponse = webClient.post()
                .uri("/api/v1/access_token")
                .header(HttpHeaders.AUTHORIZATION, "Basic " + clientAuth)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("grant_type", "refresh_token")
                        .with("refresh_token", redditAccount.getRefreshToken()))
                .retrieve()
                .bodyToMono(TokenResponse.class);

        TokenResponse tokens = tokenResponse.block();
        if (tokens == null || tokens.getAccessToken() == null) {
            throw new RuntimeException("Nie udało się odświeżyć tokena Reddita");
        }

        redditAccount.setAccessToken(tokens.getAccessToken());
        redditAccount.setTokenExpiry(Instant.now().plusSeconds(tokens.getExpiresIn()));
        user.setRedditAccount(redditAccount);
        userRepository.save(user);

        return redditAccount.getAccessToken();
    }



    @GetMapping("/me")
    public ResponseEntity<?> getRedditProfile(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUserName(userDetails.getUsername());
        if (user == null || user.getRedditAccount() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Reddit account not linked.");
        }

        String accessToken = getValidAccessToken(user); // logika sprawdza ważność i odświeża token

        String profile = WebClient.create("https://oauth.reddit.com")
                .get()
                .uri("/api/v1/me")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return ResponseEntity.ok(profile);
    }

}

