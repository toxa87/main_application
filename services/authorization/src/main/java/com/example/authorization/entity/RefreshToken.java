package com.example.authorization.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "refresh_token", schema="access_users")
public class RefreshToken {

    @Id
    @Column(name = "refresh_token_id", nullable = false, unique = true)
    @GeneratedValue
    private UUID refreshTokenId;

    @Column(name="refresh_token", nullable = false, unique = true)
    private String refreshToken;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "expired_at")
    private LocalDateTime expiredAt;

    @Column(name = "revoked_at")
    private Boolean revokedAt;

}
