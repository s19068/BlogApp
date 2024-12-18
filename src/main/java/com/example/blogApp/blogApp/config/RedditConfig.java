package com.example.blogApp.blogApp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Base64;

@Configuration
public class RedditConfig {

    /*@Value("${spring.security.oauth2.client.registration.reddit.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.reddit.client-secret}")
    private String clientSecret;

    @Bean
    public WebClient redditWebClient() {
        return WebClient.builder()
                .baseUrl("https://oauth.reddit.com")
                .defaultHeader(HttpHeaders.USER_AGENT, "web:blogapp:v1.0 (by /u/Designer-Mission4930)")
                .filter(ExchangeFilterFunction.ofRequestProcessor(
                        clientCredentials -> Mono.just(clientCredentials)
                                .map(request -> {
                                    String auth = Base64.getEncoder()
                                            .encodeToString((clientId + ":" + clientSecret).getBytes());
                                    request.headers().add("Authorization", "Basic " + auth);
                                    return request;
                                })
                ))
                .build();
    }*/



        @Bean
        public WebClient redditWebClient(OAuth2AuthorizedClientManager authorizedClientManager) {
            ServletOAuth2AuthorizedClientExchangeFilterFunction oauth2Client =
                    new ServletOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);
            oauth2Client.setDefaultClientRegistrationId("reddit");

            return WebClient.builder()
                    .baseUrl("https://oauth.reddit.com")
                    .defaultHeader(HttpHeaders.USER_AGENT, "java:com.example.blogapp:v1.0")
                    .apply(oauth2Client.oauth2Configuration())
                    .build();
        }

        @Bean
        public OAuth2AuthorizedClientManager authorizedClientManager(
                ClientRegistrationRepository clientRegistrationRepository,
                OAuth2AuthorizedClientRepository authorizedClientRepository) {

            OAuth2AuthorizedClientProvider authorizedClientProvider =
                    OAuth2AuthorizedClientProviderBuilder.builder()
                            .authorizationCode()
                            .refreshToken()
                            .build();

            DefaultOAuth2AuthorizedClientManager authorizedClientManager =
                    new DefaultOAuth2AuthorizedClientManager(
                            clientRegistrationRepository, authorizedClientRepository);
            authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);

            return authorizedClientManager;
        }

}