package com.example.authorization.repository;

import com.example.authorization.entity.AccessTokenBlacklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccessTokenBlacklistRepository extends JpaRepository<AccessTokenBlacklist, String> {
}
