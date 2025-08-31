package com.example.youthy.repository;

import com.example.youthy.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByJti(String jti);
    Optional<RefreshToken> findByTokenHash(String tokenHash);
    List<RefreshToken> findAllByMemberId(Long memberId);
    long deleteByMemberId(Long memberId);
    long deleteByExpiresAtBefore(Instant t); // 청소용
}
