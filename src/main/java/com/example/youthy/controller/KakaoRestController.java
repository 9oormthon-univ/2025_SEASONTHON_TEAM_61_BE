// src/main/java/com/example/youthy/controller/KakaoRestController.java
package com.example.youthy.controller;

import com.example.youthy.dto.Tokens; // DTO (com.example.youthy.dto.Tokens)
import com.example.youthy.service.KakaoService;
import com.example.youthy.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/kakao")
@Validated
@Tag(name = "kakao-rest-controller", description = "Kakao OAuth & Auth")
public class KakaoRestController {

    // 운영(HTTPS) 쿠키 설정
    private static final String COOKIE_DOMAIN   = null;   // 필요 시 ".yourdomain.com"
    private static final boolean COOKIE_SECURE  = true;   // HTTPS
    private static final String COOKIE_SAMESITE = "None"; // 크로스사이트 허용
    private static final int REFRESH_MAX_AGE    = 60 * 60 * 24 * 7;

    private final KakaoService kakaoService;
    private final TokenService tokenService;

    @Operation(summary = "카카오 코드 교환", description = "프론트로 받은 code+redirectUri로 카카오 토큰을 교환하고, 서비스 Access/Refresh를 발급합니다.")
    @PostMapping("/auth/code")
    public ResponseEntity<?> exchangeCode(@RequestBody AuthCodeReq req,
                                          HttpServletRequest httpReq,
                                          HttpServletResponse httpRes) {

        if (!StringUtils.hasText(req.code) || !StringUtils.hasText(req.redirectUri)) {
            return ResponseEntity.badRequest().body(Map.of("status","error","message","code and redirectUri are required"));
        }

        // 1) redirectUri 화이트리스트 검사
        kakaoService.validateRedirectOrigin(req.redirectUri);

        // 2) 카카오 access_token 교환 (redirectUri 반드시 포함)
        String kakaoAccessToken = kakaoService.getAccessTokenWithRedirect(req.code, req.redirectUri);

        // 3) 카카오 유저 정보 조회
        Map<String, Object> userInfo = kakaoService.getUserInfo(kakaoAccessToken);

        // 4) 회원 upsert + 우리 토큰 발급 (UA/IP 로깅)
        String ua = firstNonEmpty(httpReq.getHeader("User-Agent"), "unknown");
        String ip = firstNonEmpty(httpReq.getHeader("X-Forwarded-For"), httpReq.getRemoteAddr());

        // KakaoService는 반드시 dto.Tokens를 반환해야 함
        Tokens tokens = kakaoService.processUser(userInfo, ua, ip);

        // 5) Refresh = HttpOnly 쿠키(운영 속성), Access = JSON
        CookieWriter.writeRefreshCookie(httpRes, tokens.getRefresh(), REFRESH_MAX_AGE, COOKIE_DOMAIN, COOKIE_SECURE, COOKIE_SAMESITE);

        return ResponseEntity.ok(Map.of(
                "access", tokens.getAccess(),
                "tokenType", "Bearer",
                "expiresIn", 3600
        ));
    }

    @Operation(summary = "액세스 재발급", description = "Refresh로 새 Access를 발급하고, 회전된 새 Refresh 쿠키를 내려줍니다.")
    @PostMapping("/auth/refresh")
    public ResponseEntity<?> refresh(HttpServletRequest req,
                                     HttpServletResponse res,
                                     @RequestHeader(value = "X-Refresh", required = false) String hdrRefresh,
                                     @RequestBody(required = false) RefreshRequest body) {

        String cookieRefresh = readRefreshCookie(req);
        String bodyRefresh   = (body != null) ? body.getRefreshToken() : null;
        String provided      = firstNonEmpty(cookieRefresh, firstNonEmpty(hdrRefresh, bodyRefresh));
        if (!StringUtils.hasText(provided)) {
            return ResponseEntity.badRequest().body(Map.of("status","error","message","Refresh token missing"));
        }

        String ua = firstNonEmpty(req.getHeader("User-Agent"), "unknown");
        String ip = firstNonEmpty(req.getHeader("X-Forwarded-For"), req.getRemoteAddr());

        // TokenService는 dto.Tokens를 직접 반환해야 함
        Tokens tokens = tokenService.refresh(provided, ua, ip);

        // 회전된 새 refresh를 운영 속성으로 재발급
        CookieWriter.writeRefreshCookie(res, tokens.getRefresh(), REFRESH_MAX_AGE, COOKIE_DOMAIN, COOKIE_SECURE, COOKIE_SAMESITE);

        return ResponseEntity.ok(Map.of("status","success","access", tokens.getAccess()));
    }

    @Operation(summary = "현재 기기 로그아웃", description = "제공된 Refresh를 폐기하고, 클라이언트의 refresh 쿠키를 삭제합니다.")
    @PostMapping("/auth/logout")
    public ResponseEntity<?> logout(HttpServletRequest req,
                                    HttpServletResponse res,
                                    @RequestHeader(value = "X-Refresh", required = false) String hdrRefresh,
                                    @RequestBody(required = false) LogoutRequest body) {

        String cookieRefresh = readRefreshCookie(req);
        String bodyRefresh   = (body != null) ? body.getRefreshToken() : null;
        String provided      = firstNonEmpty(cookieRefresh, firstNonEmpty(hdrRefresh, bodyRefresh));
        if (!StringUtils.hasText(provided)) {
            return ResponseEntity.badRequest().body(Map.of("status","error","message","Refresh token missing"));
        }

        tokenService.revokeOneByRefresh(provided);
        CookieWriter.clearRefreshCookie(res, COOKIE_DOMAIN, COOKIE_SECURE, COOKIE_SAMESITE);

        return ResponseEntity.ok(Map.of("status","success","message","Logged out on this device"));
    }

    // ==== DTO ====
    public static class AuthCodeReq {
        @NotBlank public String code;
        @NotBlank public String redirectUri;
    }
    public static class RefreshRequest {
        public String refreshToken;
        public String getRefreshToken(){ return refreshToken; }
    }
    public static class LogoutRequest {
        public String refreshToken;
        public String getRefreshToken(){ return refreshToken; }
    }

    // ==== Helpers ====
    private static String firstNonEmpty(String a, String b) {
        return StringUtils.hasText(a) ? a : b;
    }
    private static String readRefreshCookie(HttpServletRequest req) {
        Cookie[] cookies = req.getCookies();
        if (cookies == null) return null;
        for (Cookie c : cookies) {
            if ("refresh".equals(c.getName())) return c.getValue();
        }
        return null;
    }

    // 쿠키 발급/삭제 유틸 (속성 동일 유지 중요)
    static class CookieWriter {
        static void writeRefreshCookie(HttpServletResponse res, String refresh, int maxAgeSeconds,
                                       String domain, boolean secure, String sameSite) {
            StringBuilder c = new StringBuilder("refresh=").append(refresh)
                    .append("; Path=/")
                    .append("; HttpOnly")
                    .append("; Max-Age=").append(maxAgeSeconds);
            if (secure) c.append("; Secure");
            if (StringUtils.hasText(domain))   c.append("; Domain=").append(domain);
            if (StringUtils.hasText(sameSite)) c.append("; SameSite=").append(sameSite);
            res.addHeader("Set-Cookie", c.toString());
        }
        static void clearRefreshCookie(HttpServletResponse res, String domain, boolean secure, String sameSite) {
            StringBuilder c = new StringBuilder("refresh=")
                    .append("; Path=/")
                    .append("; HttpOnly")
                    .append("; Max-Age=0");
            if (secure) c.append("; Secure");
            if (StringUtils.hasText(domain))   c.append("; Domain=").append(domain);
            if (StringUtils.hasText(sameSite)) c.append("; SameSite=").append(sameSite);
            res.addHeader("Set-Cookie", c.toString());
        }
    }
}
