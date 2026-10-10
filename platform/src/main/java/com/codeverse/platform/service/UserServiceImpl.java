package com.codeverse.platform.service;

import com.codeverse.platform.exception.InputValidationException;
import com.codeverse.platform.exception.ResourceNotFoundException;
import com.codeverse.platform.exception.UnauthorizedException;
import com.codeverse.platform.model.*;
import com.codeverse.platform.repository.EnrollmentJpaRepository;
import com.codeverse.platform.repository.ReviewJpaRepository;
import com.codeverse.platform.repository.UserJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserJpaRepository userRepo;
    private final EnrollmentJpaRepository enrollmentRepo;
    private final ReviewJpaRepository reviewRepo;

    public UserServiceImpl(UserJpaRepository userRepo, EnrollmentJpaRepository enrollmentRepo, ReviewJpaRepository reviewRepo) {
        this.userRepo = userRepo;
        this.enrollmentRepo = enrollmentRepo;
        this.reviewRepo = reviewRepo;
    }

    @Override
    public User registerUser(RegistrationRequest request) {
        String username = request.getUsername() == null ? "" : request.getUsername().trim();
        String password = request.getPassword() == null ? "" : request.getPassword();

        if (username.isEmpty()) throw new InputValidationException("Username cannot be empty.");
        if (password.length() < 6) throw new InputValidationException("Password must be at least 6 characters.");

        if (userRepo.findByUsername(username).isPresent()) {
            throw new InputValidationException("Username already exists.");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setRole(Role.STUDENT);

        return userRepo.save(user);
    }

    @Override
    public User loginUser(LoginRequest request) {
        String username = request.getUsername() == null ? "" : request.getUsername().trim();
        String password = request.getPassword() == null ? "" : request.getPassword();

        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new UnauthorizedException("Invalid username or password."));

        if (!user.getPassword().equals(password)) {
            throw new UnauthorizedException("Invalid username or password.");
        }

        return user;
    }

    @Override
    public List<User> getAllUsers(int adminUserId) {
        User admin = userRepo.findById(adminUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin user not found."));

        if (admin.getRole() != Role.ADMIN) {
            throw new UnauthorizedException("Only admins can list users.");
        }

        return userRepo.findAll();
    }

    @Override
    public void deleteUser(int userIdToDelete, int adminUserId) {
        User admin = userRepo.findById(adminUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin user not found."));

        if (admin.getRole() != Role.ADMIN) {
            throw new UnauthorizedException("Only admins can delete users.");
        }

        if (!userRepo.existsById(userIdToDelete)) {
            throw new ResourceNotFoundException("User not found.");
        }

        // Clean related rows
        enrollmentRepo.deleteAll(enrollmentRepo.findByUserId(userIdToDelete));
        reviewRepo.deleteAll(reviewRepo.findByUserId(userIdToDelete));

        userRepo.deleteById(userIdToDelete);
    }
}
