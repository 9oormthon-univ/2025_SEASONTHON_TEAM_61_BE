package com.example.youthy.service;

import com.example.youthy.domain.Member;
import com.example.youthy.domain.RefreshToken;
import com.example.youthy.repository.RefreshTokenRepository;
import com.example.youthy.util.HashUtil;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final RefreshTokenRepository refreshRepo;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.access-validity-seconds:3600}")
    private long accessTtl;

    @Value("${jwt.refresh-validity-seconds:1209600}") // 14d
    private long refreshTtl;

    /** 새 Access 생성 */
    public String createAccess(Member m, long customTtlSeconds) {
        long ttl = (customTtlSeconds > 0) ? customTtlSeconds : accessTtl;
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(String.valueOf(m.getKakaoId() != null ? m.getKakaoId() : m.getId()))
                .claim("memberId", m.getId())
                .claim("id", m.getId())
                .claim("kakaoId", m.getKakaoId())
                .claim("username", m.getUsername())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(ttl)))
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)))
                .compact();
    }


    /** Refresh 토큰 생성 + 저장 */
    public String mintRefresh(Member m, String userAgent, String ip) {
        String jti = UUID.randomUUID().toString();
        Instant exp = Instant.now().plusSeconds(refreshTtl);

        String refresh = "rt_" + UUID.randomUUID();
        String tokenHash = HashUtil.sha256(refresh);

        RefreshToken entity = RefreshToken.builder()
                .memberId(m.getId())
                .jti(jti)
                .tokenHash(tokenHash)
                .userAgent(safe(userAgent))
                .ip(safe(ip))
                .expiresAt(exp)
                .revoked(false)
                .rotated(false)
                .build();
        refreshRepo.save(entity);
        return refresh;
    }

    /** Access/Refresh 페어 */
    public record Tokens(String access, String refresh) {
        // 기존 DTO와 호환되는 getter 이름 추가
        public String getAccessToken() { return access; }
        public String getRefreshToken() { return refresh; }
        public String getAccess() { return access; }
        public String getRefresh() { return refresh; }
    }

    /** 회전 발급 */
    public Tokens rotateAndIssue(Member m, String oldRefreshOrNull, String userAgent, String ip) {
        if (oldRefreshOrNull != null && oldRefreshOrNull.startsWith("rt_")) {
            String oldHash = HashUtil.sha256(oldRefreshOrNull);
            refreshRepo.findByTokenHash(oldHash).ifPresent(rt -> {
                rt.setRotated(true);
                refreshRepo.save(rt);
            });
        }
        String access = createAccess(m, accessTtl);
        String refresh = mintRefresh(m, userAgent, ip);
        return new Tokens(access, refresh);
    }

    /** 재발급 */
    public Tokens refresh(String providedRefresh, String userAgent, String ip) {
        RefreshToken rt = validateRefreshToken(providedRefresh);
        // 최소 정보 Member
        Member temp = Member.builder().id(rt.getMemberId()).username("unknown").build();
        return rotateAndIssue(temp, providedRefresh, userAgent, ip);
    }

    public long revokeAllFor(Long memberId) {
        var all = refreshRepo.findAllByMemberId(memberId);
        long cnt = 0;
        for (RefreshToken rt : all) {
            if (!rt.isRevoked()) {
                rt.setRevoked(true);
                refreshRepo.save(rt);
                cnt++;
            }
        }
        return cnt;
    }

    public void revokeOneByRefresh(String refresh) {
        String hash = HashUtil.sha256(refresh);
        refreshRepo.findByTokenHash(hash).ifPresent(rt -> {
            rt.setRevoked(true);
            refreshRepo.save(rt);
        });
    }

    private static String safe(String s) {
        if (s == null) return null;
        return (s.length() > 256) ? s.substring(0, 256) : s;
    }

    /* ===================== 호환용 메서드 추가 ===================== */

    // 기존 이름: createAccessToken
    public String createAccessToken(Member m) {
        return createAccess(m, accessTtl);
    }

    // 기존 이름: issueRefreshToken
    public String issueRefreshToken(Member m, String userAgent, String ip) {
        return mintRefresh(m, userAgent, ip);
    }

    // 기존 이름: validateRefreshToken
    public RefreshToken validateRefreshToken(String providedRefresh) {
        if (providedRefresh == null || !providedRefresh.startsWith("rt_")) {
            throw new IllegalArgumentException("Refresh token missing or malformed");
        }
        String hash = HashUtil.sha256(providedRefresh);
        RefreshToken rt = refreshRepo.findByTokenHash(hash)
                .orElseThrow(() -> new IllegalArgumentException("Refresh token not found"));

        if (rt.isRevoked() || rt.isRotated() || rt.getExpiresAt().isBefore(Instant.now())) {
            throw new IllegalStateException("Refresh token is no longer valid");
        }
        return rt;
    }

    // 기존 이름: rotateRefreshToken
    public RefreshToken rotateRefreshToken(RefreshToken validRt, Member m) {
        validRt.setRotated(true);
        refreshRepo.save(validRt);
        // 새로운 refresh 발급
        String newPlain = mintRefresh(m, null, null);
        RefreshToken entity = RefreshToken.builder()
                .memberId(m.getId())
                .jti(UUID.randomUUID().toString())
                .tokenHash(HashUtil.sha256(newPlain))
                .expiresAt(Instant.now().plusSeconds(refreshTtl))
                .revoked(false)
                .rotated(false)
                .build();
        entity.setToken(newPlain); // transient 필드에 plain 담아둠
        return entity;
    }

    // 기존 이름: revokeAllForMember
    public long revokeAllForMember(Long memberId) {
        return revokeAllFor(memberId);
    }
}
