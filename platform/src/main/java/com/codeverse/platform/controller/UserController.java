package com.codeverse.platform.controller;

import jakarta.validation.Valid;
import com.codeverse.platform.model.LoginRequest;
import com.codeverse.platform.model.RegistrationRequest;
import com.codeverse.platform.model.User;
import com.codeverse.platform.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Responds to: POST http://localhost:8080/api/auth/login
    @PostMapping("/login")
    public ResponseEntity<User> loginUser(@Valid @RequestBody LoginRequest request) {
        User user = userService.loginUser(request);
        return ResponseEntity.ok(user); // Return 200 OK with the user data
    }

    // Responds to: POST http://localhost:8080/api/auth/register
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@Valid @RequestBody RegistrationRequest request) {
        User newUser = userService.registerUser(request);
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }
}