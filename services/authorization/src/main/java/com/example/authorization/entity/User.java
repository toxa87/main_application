package com.example.authorization.entity;



import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "users", schema="access_users")
public class User {
    @Id
    @Column(name = "user_id", nullable = false)
    @GeneratedValue
    private UUID userId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(name = "create_at")
    private LocalDateTime createAt = LocalDateTime.now();

    @Column(nullable = false)
    private boolean active = false;

}
