package com.codeverse.platform.controller;

import jakarta.validation.Valid;

import com.codeverse.platform.model.User;
import com.codeverse.platform.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    // Responds to: GET http://localhost:8080/api/admin/users?adminUserId=1
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers(@RequestParam int adminUserId) {
        List<User> users = userService.getAllUsers(adminUserId);
        users.forEach(user -> user.setPassword(null));
        return ResponseEntity.ok(users);
    }
    //Responds to: DELETE http://localhost:8080/api/admin/users/{id}?adminUserId={adminId}
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable int id,
            @RequestParam int adminUserId) {
        userService.deleteUser(id, adminUserId);
        return ResponseEntity.noContent().build();
    }
}
