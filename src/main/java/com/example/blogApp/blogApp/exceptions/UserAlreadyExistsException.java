package com.example.blogApp.blogApp.exceptions;

public class UserAlreadyExistsException extends Throwable {
    public UserAlreadyExistsException(String s, String userName) {
        System.out.println(s+" "+userName);
    }
}
