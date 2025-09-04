// src/main/java/com/example/youthy/config/JwtAuthFilter.java
package com.example.youthy.config;

import com.example.youthy.domain.Member;
import com.example.youthy.repository.MemberRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.List;
import java.util.Optional;

public class JwtAuthFilter extends OncePerRequestFilter {

    private final Key key;
    private final MemberRepository memberRepository;

    public JwtAuthFilter(String jwtSecret, MemberRepository memberRepository) {
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        this.memberRepository = memberRepository;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        // 보호 경로: /api/**, /kakao/auth/logout 만 필터 적용
        boolean needsAuth = uri.startsWith("/api/") || "/kakao/auth/logout".equals(uri);
        return !needsAuth; // 보호 경로가 아니면 필터 스킵
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        try {
            String token = resolveAccessTokenFromCookie(request);
            if (token == null) {
                unauthorized(response, "Missing access_token cookie");
                return;
            }

            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String kakaoIdStr = claims.getSubject();
            Integer vInToken = claims.get("v", Integer.class);  // ★ 버전 클레임 추출
            if (kakaoIdStr == null || kakaoIdStr.isBlank() || vInToken == null) {
                unauthorized(response, "Invalid token subject/version");
                return;
            }

            Long kakaoId = Long.parseLong(kakaoIdStr);
            Member member = memberRepository.findByKakaoId(kakaoId).orElse(null);
            if (member == null) {
                unauthorized(response, "Member not found for token subject");
                return;
            }

// ★ 서버(DB) 버전과 토큰 버전 일치 확인
            if (member.getTokenVersion() != vInToken.intValue()) {
                unauthorized(response, "Token version mismatch");
                return;
            }

            var auth = new UsernamePasswordAuthenticationToken(member, null, List.of());
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(auth);

            chain.doFilter(request, response);

        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            unauthorized(response, "Token expired");
        } catch (io.jsonwebtoken.JwtException e) {
            unauthorized(response, "Invalid token");
        } catch (Exception e) {
            unauthorized(response, "Auth error: " + e.getMessage());
        }
    }

    private String resolveAccessTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
        for (Cookie c : cookies) {
            if ("access_token".equals(c.getName())) {
                String v = c.getValue();
                if (v != null && !v.isBlank()) return v.trim();
            }
        }
        return null;
    }

    private Optional<Long> parseLongSafe(String s) {
        try { return Optional.of(Long.parseLong(s)); }
        catch (NumberFormatException e) { return Optional.empty(); }
    }

    private void unauthorized(HttpServletResponse response, String message) throws IOException {
        if (!response.isCommitted()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"error\":\"Unauthorized\",\"message\":\"" + message + "\"}");
        }
    }
}
