package com.example.blogApp.blogApp.repository;

import com.example.blogApp.blogApp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUserName(String username);

    boolean existsByUserName(String username);
}
