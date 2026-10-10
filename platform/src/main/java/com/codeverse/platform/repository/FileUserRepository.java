package com.codeverse.platform.repository;

import com.codeverse.platform.model.User;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@ConditionalOnProperty(name = "codeverse.storage", havingValue = "file")
public class FileUserRepository {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final File file;

    public FileUserRepository(@Value("${data.users.path}") String path) {
        this.file = new File(path);
    }

    public List<User> findAll() {
        try {
            if (!file.exists()) {
                return new ArrayList<>();
            }
            List<User> loaded = objectMapper.readValue(file, new TypeReference<List<User>>() {});
            return new ArrayList<>(loaded); // mutable
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    public Optional<User> findById(int id) {
        return findAll().stream().filter(u -> u.getId() == id).findFirst();
    }

    public Optional<User> findByUsername(String username) {
        return findAll().stream()
                .filter(u -> u.getUsername() != null && u.getUsername().equalsIgnoreCase(username))
                .findFirst();
    }

    public User save(User user) {
        List<User> users = findAll();

        if (user.getId() == 0) {
            int nextId = users.stream().mapToInt(User::getId).max().orElse(0) + 1;
            user.setId(nextId);
            users.add(user);
        } else {
            boolean updated = false;
            for (int i = 0; i < users.size(); i++) {
                if (users.get(i).getId() == user.getId()) {
                    users.set(i, user);
                    updated = true;
                    break;
                }
            }
            if (!updated) {
                users.add(user);
            }
        }

        writeAll(users);
        return user;
    }

    private void writeAll(List<User> users) {
        try {
            file.getParentFile().mkdirs();
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, users);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write users to file: " + file.getAbsolutePath(), e);
        }
    }
}
