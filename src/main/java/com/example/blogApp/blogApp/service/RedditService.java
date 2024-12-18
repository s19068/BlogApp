package com.example.blogApp.blogApp.service;

import com.example.blogApp.blogApp.model.RedditChild;
import com.example.blogApp.blogApp.model.RedditPost;
import com.example.blogApp.blogApp.model.RedditResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RedditService {

    private final WebClient redditWebClient;

    public RedditService(WebClient redditWebClient) {
        this.redditWebClient = redditWebClient;
    }

    public Mono<List<RedditPost>> getTopPosts() {
        return redditWebClient.get()
                .uri("/r/AskReddit/top?limit=10")
                .retrieve()
                .bodyToMono(RedditResponse.class)
                .map(response -> response.getData().getChildren()
                        .stream()
                        .map(RedditChild::getData)
                        .collect(Collectors.toList()));
    }
}


