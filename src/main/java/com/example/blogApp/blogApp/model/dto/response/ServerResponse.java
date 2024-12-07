package com.example.blogApp.blogApp.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@AllArgsConstructor
public class ServerResponse {
    private String message;
    private HttpStatus status;
}
