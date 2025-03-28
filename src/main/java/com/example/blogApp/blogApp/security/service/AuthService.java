package com.example.blogApp.blogApp.security.service;

import com.example.blogApp.blogApp.exceptions.HttpException;
import com.example.blogApp.blogApp.model.User;
import com.example.blogApp.blogApp.model.dto.request.AuthenticationRequest;
import com.example.blogApp.blogApp.model.dto.request.RegisterRequest;
import com.example.blogApp.blogApp.model.dto.response.JwtResponse;
import com.example.blogApp.blogApp.model.enums.RoleName;
import com.example.blogApp.blogApp.model.security.Role;
import com.example.blogApp.blogApp.model.security.UserPrincipal;
import com.example.blogApp.blogApp.repository.RoleRepository;
import com.example.blogApp.blogApp.repository.UserRepository;
import com.example.blogApp.blogApp.utils.JwtUtils;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AuthService {

    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;

    public JwtResponse login(AuthenticationRequest authenticationRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authenticationRequest.getUserName(),
                        authenticationRequest.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwt(authentication);
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        Set<String> roles = userPrincipal
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        return JwtResponse
                .builder()
                .email(userPrincipal.getUsername())
                .token(jwt)
                .roles(roles)
                .userId(userPrincipal.getUserData().getUserId())
                .type("Bearer ")
                .build();
    }

    public void registerUser(RegisterRequest registerRequest) throws HttpException {
        if (userRepository.existsByUserName(registerRequest.getUserName())){
            throw new HttpException(HttpStatus.BAD_REQUEST, "Email already in use!");
        }
        User user = User.builder()
                .userName(registerRequest.getUserName())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .email(registerRequest.getEmail())
                .build();

        Role userRole = Optional.ofNullable(roleRepository.findByName(RoleName.USER.getPrefixedName()))
                .orElseThrow(() -> new RuntimeException("Role " + RoleName.USER + " not found!"));

        user.setRoles(Set.of(userRole));
        userRepository.save(user);
    }


}
