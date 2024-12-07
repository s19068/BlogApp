package com.example.blogApp.blogApp.repository;

import com.example.blogApp.blogApp.model.security.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role,Long> {
    Role findByName(String name);
}
