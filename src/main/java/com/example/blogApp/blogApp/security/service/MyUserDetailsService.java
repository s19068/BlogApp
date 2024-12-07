package com.example.blogApp.blogApp.security.service;

import com.example.blogApp.blogApp.model.security.UserPrincipal;
import com.example.blogApp.blogApp.model.User;
import com.example.blogApp.blogApp.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public MyUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = Optional.ofNullable(userRepository.findByUserName(username));
        userOptional.orElseThrow(() -> new UsernameNotFoundException(String.format("Username %s not found", username)));

        return UserPrincipal.of(userOptional.get());
    }
}
