package com.example.blogApp.blogApp.service;

import com.example.blogApp.blogApp.model.User;
import com.example.blogApp.blogApp.model.UserIntegration;
import com.example.blogApp.blogApp.model.enums.RoleName;
import com.example.blogApp.blogApp.model.security.Role;
import com.example.blogApp.blogApp.repository.RoleRepository;
import com.example.blogApp.blogApp.repository.UserIntegrationRepository;
import com.example.blogApp.blogApp.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DatabaseSeederService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserIntegrationRepository userIntegrationRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    @Transactional
    public void seedDatabase() {
        seedRoles();
        seedUsersWithRedditIntegration();
    }

    private void seedRoles() {
        if (roleRepository.count() == 0) {
            Role roleUser = new Role();
            roleUser.setName(RoleName.USER.getPrefixedName());
            Role roleAdmin = new Role();
            roleAdmin.setName(RoleName.ADMIN.getPrefixedName());
            roleRepository.save(roleUser);
            roleRepository.save(roleAdmin);
        }
    }

    private void seedUsersWithRedditIntegration() {
        if (userRepository.count() == 0) {
            Role roleUser = roleRepository.findByName("ROLE_USER");
            Role roleAdmin = roleRepository.findByName("ROLE_ADMIN");

            // Twórz użytkowników
            User user1 = createUser("Aleksander", "pass", "aleksander@gmail.com", Set.of(roleUser));
            User user2 = createUser("Maciek", "pass", "maciek@gmail.com", Set.of(roleUser));
            User user3 = createUser("Tomasz", "pass", "tomasz@gmail.com", Set.of(roleAdmin));

            userRepository.saveAll(List.of(user1, user2, user3));

            // Dodaj integrację Reddit do użytkowników
            //seedRedditIntegrationForUser(user1);
            //seedRedditIntegrationForUser(user2);
        }
    }

    private User createUser(String userName, String password, String email, Set<Role> roles) {
        return User.builder()
                .userName(userName)
                .password(passwordEncoder.encode(password))
                .email(email)
                .roles(roles)
                .build();
    }

    /*private void seedRedditIntegrationForUser(User user) {
        UserIntegration redditIntegration = UserIntegration.builder()
                .providerName("reddit")
                .clientId("JH4N1Ilr3P_c48p3T1su0w")
                .clientSecret("av8NnmONjZQiMGGAYkAhcpB5fZ8Pig")
                .redirectUri("http://localhost:8080/login/oauth2/code/reddit")
                .authorizationUri("https://www.reddit.com/api/v1/authorize")
                .tokenUri("https://www.reddit.com/api/v1/access_token")
                .userInfoUri("https://oauth.reddit.com/api/v1/me")
                .userNameAttribute("name")
                .scopes(List.of("identity", "read"))
                .user(user)
                .build();

        userIntegrationRepository.save(redditIntegration);
    }*/
}

    /*@PostConstruct
    @Transactional
    public void seedDatabase() {
        if (roleRepository.count() == 0) {
            Role roleUser = new Role();
            roleUser.setName(RoleName.USER.getPrefixedName());
            Role roleAdmin = new Role();
            roleAdmin.setName(RoleName.ADMIN.getPrefixedName());
            roleRepository.save(roleUser);
            roleRepository.save(roleAdmin);

            User user1 = User.builder()
                    .userName("Aleksander")
                    .password(passwordEncoder.encode("pass"))
                    .email("aleksander@gmail.com")
                    .roles(Set.of(roleUser))
                    .build();

            User user2 = User.builder()
                    .userName("Maciek")
                    .password(passwordEncoder.encode("pass"))
                    .email("maciek@gmail.com")
                    .roles(Set.of(roleUser))
                    .build();

            User user3 = User.builder()
                    .userName("Tomasz")
                    .password(passwordEncoder.encode("pass"))
                    .email("tomasz@gmail.com")
                    .roles(Set.of(roleAdmin))
                    .build();

            userRepository.save(user1);
            userRepository.save(user2);
            userRepository.save(user3);
        }
    }*/
/*    @PostConstruct
    @Transactional
    public void seedDatabase() {
        if (roleRepository.count() == 0) {
            seedRoles();
        }
        if (userRepository.count() == 0) {
            seedUsersWithRedditIntegration();
        }
    }

    private void seedRoles() {
        Role roleUser = new Role();
        roleUser.setName(RoleName.USER.getPrefixedName());
        Role roleAdmin = new Role();
        roleAdmin.setName(RoleName.ADMIN.getPrefixedName());
        roleRepository.save(roleUser);
        roleRepository.save(roleAdmin);
    }

    private void seedUsersWithRedditIntegration() {
        Role roleUser = roleRepository.findByName(RoleName.USER.getPrefixedName());
        Role roleAdmin = roleRepository.findByName(RoleName.ADMIN.getPrefixedName());

        User user1 = User.builder()
                .userName("Aleksander")
                .password(passwordEncoder.encode("pass"))
                .email("aleksander@gmail.com")
                .roles(Set.of(roleUser))
                .build();

        User user2 = User.builder()
                .userName("Maciek")
                .password(passwordEncoder.encode("pass"))
                .email("maciek@gmail.com")
                .roles(Set.of(roleUser))
                .build();

        User user3 = User.builder()
                .userName("Tomasz")
                .password(passwordEncoder.encode("pass"))
                .email("tomasz@gmail.com")
                .roles(Set.of(roleAdmin))
                .build();

        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);

        seedRedditIntegrationForUser(user1);
        seedRedditIntegrationForUser(user2);
        seedRedditIntegrationForUser(user3);
    }

    private void seedRedditIntegrationForUser(User user) {
        UserIntegration redditIntegration = UserIntegration.builder()
                .providerName("reddit")
                .clientId("JH4N1Ilr3P_c48p3T1su0w")
                .clientSecret("av8NnmONjZQiMGGAYkAhcpB5fZ8Pig")
                .redirectUri("http://localhost:8080/login/oauth2/code/reddit")
                .authorizationUri("https://www.reddit.com/api/v1/authorize")
                .tokenUri("https://www.reddit.com/api/v1/access_token")
                .userInfoUri("https://oauth.reddit.com/api/v1/me")
                .userNameAttribute("name")
                .scopes(List.of("identity", "read"))
                .user(user) // Powiąż integrację z użytkownikiem
                .build();

        userIntegrationRepository.save(redditIntegration);
    }
}*/
