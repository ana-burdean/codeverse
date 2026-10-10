package com.codeverse.platform.repository;

import com.codeverse.platform.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);
}
