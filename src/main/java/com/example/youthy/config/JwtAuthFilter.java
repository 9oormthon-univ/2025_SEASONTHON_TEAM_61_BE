package com.example.youthy.config;

import com.example.youthy.domain.Member;
import com.example.youthy.repository.MemberRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class JwtAuthFilter extends OncePerRequestFilter {

    private final String jwtSecret;
    private final MemberRepository memberRepository;

    public JwtAuthFilter(String jwtSecret, MemberRepository memberRepository) {
        this.jwtSecret = jwtSecret;
        this.memberRepository = memberRepository;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // /api/** 만 보호
        return !request.getRequestURI().startsWith("/api/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws ServletException, IOException {

        // ✅ CORS preflight 는 바로 통과
        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
            chain.doFilter(req, res);
            return;
        }

        String header = req.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            writeJson(res, HttpServletResponse.SC_UNAUTHORIZED,
                    "Missing Bearer token");
            return;
        }

        String token = header.substring(7);
        try {
            // JJWT 0.11.x: setSigningKey + parseClaimsJws
            Claims claims = Jwts.parser()
                    .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)))
                    .parseClaimsJws(token)
                    .getBody();

            // 우선 memberId, 없으면 id 허용(호환)
            Long memberId = claims.get("memberId", Long.class);
            if (memberId == null) {
                Object idObj = claims.get("id");
                if (idObj instanceof Number num) {
                    memberId = num.longValue();
                } else if (idObj != null) {
                    try {
                        memberId = Long.parseLong(idObj.toString());
                    } catch (NumberFormatException ignore) { /* 무시 */ }
                }
            }

            if (memberId == null) {
                writeJson(res, HttpServletResponse.SC_UNAUTHORIZED,
                        "Missing member id claim");
                return;
            }

            Optional<Member> opt = memberRepository.findById(memberId);
            if (opt.isEmpty()) {
                writeJson(res, HttpServletResponse.SC_UNAUTHORIZED,
                        "User not found");
                return;
            }

            // 컨트롤러에서 @CurrentMember 로 주입받도록 setAttribute
            req.setAttribute("authMember", opt.get());
            chain.doFilter(req, res);

        } catch (JwtException e) {
            // 서명 불일치/만료/손상 등 JWT 관련 예외
            writeJson(res, HttpServletResponse.SC_UNAUTHORIZED,
                    "Invalid or expired token");
        }
    }

    private static void writeJson(HttpServletResponse res, int status, String message) throws IOException {
        res.setStatus(status);
        res.setContentType("application/json;charset=UTF-8");
        res.getWriter().write("{\"status\":\"error\",\"message\":\"" + message + "\"}");
    }
}
