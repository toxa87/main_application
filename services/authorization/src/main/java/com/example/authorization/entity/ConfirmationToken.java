package com.example.authorization.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "confirmation_token", schema="access_users")
public class ConfirmationToken {

    @Id
    @Column(name = "confirmation_token_id", nullable = false, unique = true)
    @GeneratedValue
    private UUID confirmationTokenId;

    @Column(name="token", unique = true)
    private String token;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "expired_at")
    private LocalDateTime expiredAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
