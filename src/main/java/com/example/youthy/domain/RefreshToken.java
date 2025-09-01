package com.example.youthy.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "refresh_token",
        indexes = {
                @Index(name = "idx_ref_member", columnList = "memberId"),
                @Index(name = "idx_ref_jti", columnList = "jti")
        })
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class RefreshToken {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long memberId;

    @Column(nullable = false, unique = true)
    private String jti;

    @Column(nullable = false, length = 128)
    private String tokenHash;

    private String userAgent;
    private String ip;

    private Instant expiresAt;
    private boolean revoked;
    private boolean rotated;

    private Instant createdAt;
    private Instant updatedAt;

    /** plain 문자열 토큰을 컨트롤러/서비스로 전달하기 위한 일시적 필드 (DB에 저장 안 함) */
    @Transient
    private String token;

    @PrePersist
    void prePersist() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }
    @PreUpdate
    void preUpdate() { updatedAt = Instant.now(); }
}
