package com.example.youthy.controller;

import com.example.youthy.config.CurrentMember;
import com.example.youthy.domain.Member;
import com.example.youthy.repository.MemberRepository;
import com.example.youthy.service.TokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final TokenService tokenService;
    private final MemberRepository memberRepository;

    /** 클라이언트: refresh 전달(쿠키 또는 body/header) → 새 access(+새 refresh 회전 발급) */
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(HttpServletRequest req, HttpServletResponse res,
                                     @RequestHeader(value = "X-Refresh", required = false) String hdrRefresh,
                                     @RequestParam(value = "refresh", required = false) String bodyRefresh) {

        String cookieRefresh = readRefreshCookie(req);
        String provided = firstNonEmpty(cookieRefresh, hdrRefresh, bodyRefresh);
        if (!StringUtils.hasText(provided)) {
            return ResponseEntity.badRequest().body(Map.of("status","error","message","Refresh token missing"));
        }

        var tokens = tokenService.refresh(provided, req.getHeader("User-Agent"), clientIp(req));
        // 쿠키로 내려주고 싶으면:
        setRefreshCookie(res, tokens.refresh());

        return ResponseEntity.ok(Map.of(
                "status","success",
                "access", tokens.access()
        ));
    }

    /** 현재 기기만 로그아웃: 전달된 refresh(쿠키/헤더/바디) 한 개 폐기 */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest req, HttpServletResponse res,
                                    @RequestHeader(value = "X-Refresh", required = false) String hdrRefresh,
                                    @RequestParam(value = "refresh", required = false) String bodyRefresh) {

        String cookieRefresh = readRefreshCookie(req);
        String provided = firstNonEmpty(cookieRefresh, hdrRefresh, bodyRefresh);
        if (!StringUtils.hasText(provided)) {
            return ResponseEntity.badRequest().body(Map.of("status","error","message","Refresh token missing"));
        }
        tokenService.revokeOneByRefresh(provided);

        // 쿠키 제거
        clearRefreshCookie(res);
        return ResponseEntity.ok(Map.of("status","success","message","Logged out on this device"));
    }

    /** 전체 기기 로그아웃: 모든 refresh 폐기 */
    @PostMapping("/logout-all")
    public ResponseEntity<?> logoutAll(@CurrentMember Member member, HttpServletResponse res) {
        if (member == null) return ResponseEntity.status(401).body(Map.of("status","error","message","Unauthorized"));
        long n = tokenService.revokeAllFor(member.getId());
        clearRefreshCookie(res);
        return ResponseEntity.ok(Map.of("status","success","revoked", n));
    }

    private static String clientIp(HttpServletRequest req) {
        String fwd = req.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(fwd)) return fwd.split(",")[0].trim();
        return req.getRemoteAddr();
    }

    private static String readRefreshCookie(HttpServletRequest req) {
        if (req.getCookies() == null) return null;
        for (Cookie c : req.getCookies()) {
            if ("refresh".equals(c.getName())) return c.getValue();
        }
        return null;
    }

    private static void setRefreshCookie(HttpServletResponse res, String refresh) {
        Cookie c = new Cookie("refresh", refresh);
        c.setHttpOnly(true);
        c.setPath("/");
        c.setMaxAge(60 * 60 * 24 * 14); // 14d
        // 개발 환경이라 Secure/SameSite 생략, 배포시:
        // c.setSecure(true); // https
        res.addCookie(c);
    }

    private static void clearRefreshCookie(HttpServletResponse res) {
        Cookie c = new Cookie("refresh", "");
        c.setPath("/");
        c.setMaxAge(0);
        res.addCookie(c);
    }

    private static String firstNonEmpty(String... vals) {
        for (String v : vals) if (StringUtils.hasText(v)) return v;
        return null;
    }
}
