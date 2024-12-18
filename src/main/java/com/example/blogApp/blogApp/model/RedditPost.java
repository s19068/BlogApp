package com.example.blogApp.blogApp.model;

import lombok.Data;

import java.util.List;

@Data
public class RedditPost {
    private String title;
    private String selftext;
    private String author;
    private int score;
}

