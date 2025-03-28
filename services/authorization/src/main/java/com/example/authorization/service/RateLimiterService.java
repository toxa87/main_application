package com.example.authorization.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimiterService {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    public boolean tryConsume(String key) {
        Bucket bucket = buckets.computeIfAbsent(key, k ->
                Bucket4j.builder()
                        .addLimit(Bandwidth.simple(5, Duration.ofMinutes(1)))
                        .build()
        );
        return bucket.tryConsume(1);
    }
}
