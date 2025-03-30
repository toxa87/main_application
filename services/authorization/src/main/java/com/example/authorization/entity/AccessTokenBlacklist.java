package com.example.authorization.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "access_token_blacklist", schema="access_users")
public class AccessTokenBlacklist {

    @Id
    @Column(name = "access_token", nullable = false)
    private String accessToken;

    @Column(name = "expired_at")
    private LocalDateTime expiredAt;
}
