// src/main/java/com/example/youthy/controller/KakaoRestController.java
package com.example.youthy.controller;

import com.example.youthy.config.CurrentMember;
import com.example.youthy.domain.Member;
import com.example.youthy.dto.Tokens;
import com.example.youthy.repository.MemberRepository;
import com.example.youthy.service.KakaoService;
import com.example.youthy.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Hidden;

import java.time.Duration;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/kakao")
@Validated
public class KakaoRestController {

    private final KakaoService kakaoService;
    private final TokenService tokenService;
    private final MemberRepository memberRepository;

    // --- Cookie/CORS 관련 설정을 yml에서 주입 ---
    @Value("${app.cookie.secure:false}")    private boolean cookieSecure;
    @Value("${app.cookie.samesite:Lax}")    private String cookieSameSite; // None | Lax | Strict
    @Value("${app.cookie.domain:}")         private String cookieDomain;   // 예: your-domain.com
    @Value("${app.cookie.path:/}")          private String cookiePath;

    // --- Request DTOs --------------------------------------------------------
    @Data
    public static class RefreshRequest {
        private String refreshToken; // 쿠키/헤더를 못 쓸 때 대비(테스트용)
    }

    @Data
    public static class LogoutRequest {
        private String refreshToken; // 쿠키/헤더를 못 쓸 때 대비(테스트용)
    }

    // --- 콜백(프론트에서 받은 code를 백엔드로 전달) --------------------------
    /** GET /kakao/callback?code=... : 카카오 인가코드로 로그인 완료 */
    @GetMapping("/callback")
    public ResponseEntity<?> callback(@RequestParam("code") @NotBlank String code,
                                      HttpServletResponse res) {
        try {
            // 1) 카카오 access_token 교환
            String kakaoAccess = kakaoService.getAccessToken(code);
            // 2) 사용자 정보 조회
            Map<String, Object> userInfo = kakaoService.getUserInfo(kakaoAccess);
            // 3) 회원 upsert + access/refresh 발급
            Tokens tokens = kakaoService.processUser(userInfo);

            // 4) refresh를 HttpOnly 쿠키로 내려주고, access는 바디로 반환
            setRefreshCookie(res, tokens.getRefresh());

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "accessToken", tokens.getAccess()  // ✅ 키 통일
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

        com.example.youthy.service.TokenService.Tokens tokens = tokenService.refresh(
                provided,
                req.getHeader("User-Agent"),
                clientIp(req)
        );

        // 회전된 새 refresh를 쿠키로 내려줌
        setRefreshCookie(res, tokens.getRefresh());

        return ResponseEntity.ok(Map.of(
                "status","success",
                "accessToken", tokens.getAccess()   // ✅ 키 통일
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
        long cnt = tokenService.revokeAllForMember(member.getId());
        clearRefreshCookie(res);
        return ResponseEntity.ok(Map.of(
                "status","success","revokedCount", cnt
        ));
    }

    /** GET /kakao/logout-url : 카카오 계정 로그아웃 URL 제공(선택) */
    @Hidden
    @GetMapping("/logout-url")
    public ResponseEntity<?> logoutUrl() {
        return ResponseEntity.ok(Map.of(
                "status","success",
                "logoutUrl", kakaoService.buildKakaoLogoutUrl()
        ));
    }

    // --- Helpers -------------------------------------------------------------

    private void setRefreshCookie(HttpServletResponse res, String refresh) {
        ResponseCookie.ResponseCookieBuilder b = ResponseCookie.from("refresh", refresh)
                .httpOnly(true)
                .secure(cookieSecure)
                .path(cookiePath)
                .maxAge(Duration.ofDays(14));

        // 빌더에서 지원하지 않는 속성은 문자열로 보강 (버전 호환)
        String cookieStr = b.build().toString();
        if (StringUtils.hasText(cookieSameSite)) {
            cookieStr += "; SameSite=" + cookieSameSite; // None / Lax / Strict
        }
        if (StringUtils.hasText(cookieDomain)) {
            cookieStr += "; Domain=" + cookieDomain;
        }

        res.addHeader("Set-Cookie", cookieStr);
    }

    private void clearRefreshCookie(HttpServletResponse res) {
        ResponseCookie.ResponseCookieBuilder b = ResponseCookie.from("refresh", "")
                .httpOnly(true)
                .secure(cookieSecure)
                .path(cookiePath)
                .maxAge(0);

        String cookieStr = b.build().toString();
        if (StringUtils.hasText(cookieSameSite)) {
            cookieStr += "; SameSite=" + cookieSameSite;
        }
        if (StringUtils.hasText(cookieDomain)) {
            cookieStr += "; Domain=" + cookieDomain;
        }

        res.addHeader("Set-Cookie", cookieStr);
    }

    private static String readRefreshCookie(HttpServletRequest req) {
        if (req.getCookies() == null) return null;
        for (var c : req.getCookies()) {
            if ("refresh".equals(c.getName())) {
                return c.getValue();
            }
        }
        return null;
    }

    private static String firstNonEmpty(String... vals) {
        if (vals == null) return null;
        for (String v : vals) {
            if (StringUtils.hasText(v)) return v;
        }
        return null;
    }

    private static String clientIp(HttpServletRequest req) {
        String xff = req.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(xff)) {
            return xff.split(",")[0].trim();
        }
        return req.getRemoteAddr();
    }
}
