package com.codeverse.platform.repository;

import com.codeverse.platform.model.User;
import java.util.Optional;
import java.util.List;

public interface UserRepository {
    Optional<User> findByUsername(String username);
    User save(User user);
    Optional<User> findById(int id);
    List<User> findAll();
    void deleteById(int id);// <-- Add this line
}