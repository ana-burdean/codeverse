package com.codeverse.platform.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "users",
       uniqueConstraints = @UniqueConstraint(name = "uq_users_username", columnNames = {"username"}))
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    public User() {
    }

    public User(int id, String username, String password, Role role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
    }
}
