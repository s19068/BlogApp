package com.example.blogApp.blogApp.service;

import com.example.blogApp.blogApp.model.User;
import com.example.blogApp.blogApp.model.enums.RoleName;
import com.example.blogApp.blogApp.model.security.Role;
import com.example.blogApp.blogApp.repository.RoleRepository;
import com.example.blogApp.blogApp.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@AllArgsConstructor
public class DatabaseSeederService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
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
    }
}
