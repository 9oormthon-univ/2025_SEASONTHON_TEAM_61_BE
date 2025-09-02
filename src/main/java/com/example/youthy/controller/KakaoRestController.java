// src/main/java/com/example/youthy/controller/KakaoRestController.java
package com.example.youthy.controller;

import com.example.youthy.config.CurrentMember;
import com.example.youthy.domain.Member;
import com.example.youthy.dto.Tokens;
import com.example.youthy.repository.MemberRepository;
import com.example.youthy.service.KakaoService;
import com.example.youthy.service.TokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Hidden;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/kakao")
@Validated
public class KakaoRestController {

    private final KakaoService kakaoService;
    private final TokenService tokenService;
    private final MemberRepository memberRepository;

    // --- Request DTOs --------------------------------------------------------

    @Data
    public static class RefreshRequest {
        // 바디로 전달 시 { "refreshToken": "rt_xxx" }
        @NotBlank(message = "refreshToken is required")
        private String refreshToken;
    }

    @Data
    public static class LogoutRequest {
        // 바디로 전달 시 { "refreshToken": "rt_xxx" } (쿠키/헤더가 있으면 생략 가능)
        private String refreshToken;
    }

    // --- Endpoints -----------------------------------------------------------

    /** GET /kakao/callback?code=... : 카카오 로그인 콜백 */
    @GetMapping("/callback")
    public ResponseEntity<?> callback(@RequestParam("code") String code,
                                      HttpServletRequest req,
                                      HttpServletResponse res) {
        try {
            String accessTokenFromKakao = kakaoService.getAccessToken(code); // 카카오 임시 code로 카카오 정식 token 발급
            Map<String, Object> userInfo = kakaoService.getUserInfo(accessTokenFromKakao); // token으로 유저 정보 조회

            // 회원 upsert + access/refresh 발급 (TokenService 내부 회전 규칙 일관 적용)
            Tokens tokens = kakaoService.processUser(userInfo);

            // refresh를 HttpOnly 쿠키로 내려주고, access는 바디로 반환
            setRefreshCookie(res, tokens.getRefresh());

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "accessToken", tokens.getAccess()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }

    /** POST /kakao/auth/refresh : refresh로 새 access token 발급(회전) */
    @PostMapping("/auth/refresh")
    public ResponseEntity<?> refresh(HttpServletRequest req,
                                     HttpServletResponse res,
                                     @RequestHeader(value = "X-Refresh", required = false) String hdrRefresh,
                                     @RequestBody(required = false) RefreshRequest body // 쿠키/헤더 쓰면 바디 생략 가능
    ) {
        String cookieRefresh = readRefreshCookie(req);
        String bodyRefresh = (body != null) ? body.getRefreshToken() : null;
        String provided = firstNonEmpty(cookieRefresh, hdrRefresh, bodyRefresh);

        if (!StringUtils.hasText(provided)) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status","error","message","Refresh token missing"
            ));
        }

        var tokens = tokenService.refresh(provided,
                req.getHeader("User-Agent"), clientIp(req));

        // 회전된 새 refresh를 쿠키로 내려줌
        setRefreshCookie(res, tokens.refresh());

        return ResponseEntity.ok(Map.of(
                "status","success",
                "access", tokens.access()
        ));
    }

    /** POST /kakao/auth/logout : 현재 디바이스 로그아웃(해당 refresh만 폐기) */
    @PostMapping("/auth/logout")
    public ResponseEntity<?> logout(HttpServletRequest req,
                                    HttpServletResponse res,
                                    @RequestHeader(value = "X-Refresh", required = false) String hdrRefresh,
                                    @RequestBody(required = false) LogoutRequest body
    ) {
        String cookieRefresh = readRefreshCookie(req);
        String bodyRefresh = (body != null) ? body.getRefreshToken() : null;
        String provided = firstNonEmpty(cookieRefresh, hdrRefresh, bodyRefresh);

        if (!StringUtils.hasText(provided)) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status","error","message","Refresh token missing"
            ));
        }

        tokenService.revokeOneByRefresh(provided);
        clearRefreshCookie(res);

        return ResponseEntity.ok(Map.of(
                "status","success","message","Logged out on this device"
        ));
    }

    /** POST /kakao/auth/logout-all : 모든 기기에서 로그아웃(멤버 전체 refresh 폐기) */
    @Hidden
    @PostMapping("/auth/logout-all")
    public ResponseEntity<?> logoutAll(@CurrentMember Member member,
                                       HttpServletResponse res) {
        if (member == null) {
            return ResponseEntity.status(401).body(Map.of(
                    "status","error","message","Unauthorized"
            ));
        }
        long n = tokenService.revokeAllFor(member.getId());
        clearRefreshCookie(res);
        return ResponseEntity.ok(Map.of(
                "status","success",
                "revoked", n
        ));
    }

    /** GET /kakao/logout-url : 카카오 로그아웃 리디렉트 URL */
    @Hidden
    @GetMapping("/logout-url")
    public ResponseEntity<?> logoutUrl() {
        return ResponseEntity.ok(Map.of(
                "logoutUrl", kakaoService.buildKakaoLogoutUrl()
        ));
    }

    // --- Helpers -------------------------------------------------------------

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
        c.setMaxAge(60 * 60 * 24 * 14); // 14일
        // 운영 시 HTTPS라면: c.setSecure(true);
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
