package com.example.youthy.service.impl;

import com.example.youthy.domain.Member;
import com.example.youthy.dto.Tokens;
import com.example.youthy.service.TokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Service
public class TokenServiceImpl implements TokenService {

    private final Key key;
    private final long accessTtlMs;
    private final long refreshTtlMs;

    public TokenServiceImpl(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-validity-seconds:900}") long accessTtl,          // 15분 기본
            @Value("${jwt.refresh-validity-seconds:1209600}") long refreshTtl     // 14일 기본
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTtlMs = accessTtl * 1000L;
        this.refreshTtlMs = refreshTtl * 1000L;
    }

    @Override
    public String issueAccessToken(String kakaoId, int tokenVersion) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(kakaoId)
                .claim("v", tokenVersion)           // ★ 버전 클레임
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + accessTtlMs))
                .signWith(key)
                .compact();
    }

    @Override
    public String issueRefreshToken(String kakaoId, int tokenVersion) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(kakaoId)
                .claim("v", tokenVersion)           // ★ 동일
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + refreshTtlMs))
                .signWith(key)
                .compact();
    }

    @Override
    public boolean validate(String jwt) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwt);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String getSubject(String jwt) {
        Claims c = Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(jwt).getBody();
        return c.getSubject();
    }

    @Override
    public Integer getVersion(String jwt) {
        Claims c = Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(jwt).getBody();
        return c.get("v", Integer.class);
    }

    @Override
    public void revokeRefresh(String refreshJwt) {
        // 무상태 전략: 저장/블랙리스트 미사용. NOP.
    }

    @Override
    public Tokens generateTokens(Member member) {
        String kakaoId = String.valueOf(member.getKakaoId());
        int v = member.getTokenVersion();

        String access = issueAccessToken(kakaoId, v);
        String refresh = issueRefreshToken(kakaoId, v);

        // ⚠️ Tokens DTO 생성 방식은 프로젝트 정의에 따라 다릅니다.
        // 아래 두 줄 중 "하나"가 맞을 가능성이 큼. 맞는 쪽을 사용하세요.
        // 1) 생성자 방식:
        return new Tokens(access, refresh);
        // 2) 빌더 방식이라면 (주석 해제):
        // return Tokens.builder().access(access).refresh(refresh).build();
    }
}
