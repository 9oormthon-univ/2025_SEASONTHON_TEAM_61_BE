package com.example.youthy.controller;

import com.example.youthy.domain.Member;
import com.example.youthy.repository.MemberRepository;
import com.example.youthy.service.KakaoService;
import com.example.youthy.service.TokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/kakao")
public class KakaoRestController {

    @Value("${kakao.client.id}")
    private String kakaoClientId;

    @Value("${kakao.login.redirect}")
    private String serverCallbackRedirectUri;

    @Value("${app.front-url:http://localhost:3000}")
    private String frontBaseUrl;

    /** DEV: http → false, PROD: https → true */
    @Value("${app.cookie.secure:false}")
    private boolean secureCookie;

    private static final String SAME_SITE = "Lax";

    @Value("${jwt.access-validity-seconds:900}")
    private int accessTtlSeconds;

    @Value("${jwt.refresh-validity-seconds:1209600}")
    private int refreshTtlSeconds;

    private final KakaoService kakaoService;
    private final TokenService tokenService;
    private final MemberRepository memberRepository;

    public KakaoRestController(KakaoService kakaoService,
                               TokenService tokenService,
                               MemberRepository memberRepository) {
        this.kakaoService = kakaoService;
        this.tokenService = tokenService;
        this.memberRepository = memberRepository;
    }

    // 1) 로그인 시작
    @GetMapping("/auth/login")
    public void login(@RequestParam(value = "state", required = false) String state,
                      HttpServletResponse res) throws IOException {
        String authorizeUrl = buildAuthorizeUrl(kakaoClientId, serverCallbackRedirectUri, state);
        res.sendRedirect(authorizeUrl);
    }

    // 2) 콜백: code → kakaoId → (회원 조회/생성) → 버전 포함 JWT 발급 → 쿠키 세팅 → 프론트 이동
    @GetMapping("/callback")
    public void callback(@RequestParam("code") @NotBlank String code,
                         @RequestParam(value = "state", required = false) String state,
                         HttpServletResponse res) throws IOException {
        try {
            // 1) code -> token
            Map<String, Object> token = kakaoService.exchangeCodeForToken(code);
            if (token == null || token.get("access_token") == null) {
                res.sendRedirect(frontBaseUrl + "/login/failed");
                return;
            }
            String accessTokenFromKakao = (String) token.get("access_token");

            // 2) token -> userInfo (/v2/user/me)
            Map<String, Object> userInfo = kakaoService.getUserInfo(accessTokenFromKakao);
            if (userInfo == null || userInfo.get("id") == null) {
                res.sendRedirect(frontBaseUrl + "/login/failed");
                return;
            }

            // 3) DB 저장/갱신 + 우리 JWT 발급 (nickname/email 반영)
            var tokens = kakaoService.loginOrSignup(userInfo);

            // 4) 우리 쿠키 세팅
            setCookie(res, "access_token",  tokens.getAccessToken(),  accessTtlSeconds,  secureCookie, SAME_SITE);
            setCookie(res, "refresh_token", tokens.getRefreshToken(), refreshTtlSeconds, secureCookie, SAME_SITE);

            // 5) 프론트로 이동
            String redirect = frontBaseUrl + "/login/success";
            if (state != null && !state.isBlank()) {
                redirect += "?state=" + URLEncoder.encode(state, StandardCharsets.UTF_8);
            }
            res.sendRedirect(redirect);

        } catch (Exception e) {
            // (선택) 로그 남기기
            // log.error("Kakao callback error", e);
            res.sendRedirect(frontBaseUrl + "/login/failed");
        }
    }

    // 3) 리프레시: refresh 검증 → subject로 회원 조회 → DB 버전으로 새 access 발급
    @PostMapping("/auth/refresh")
    public ResponseEntity<?> refresh(@CookieValue(name = "refresh_token", required = false) String refreshCookie,
                                     HttpServletResponse res) {
        if (refreshCookie == null || !tokenService.validate(refreshCookie)) {
            return ResponseEntity.status(401).body(Map.of("error","Unauthorized","message","Invalid refresh cookie"));
        }
        String kakaoId = tokenService.getSubject(refreshCookie);
        Integer vInToken = tokenService.getVersion(refreshCookie);
        if (kakaoId == null || vInToken == null) {
            return ResponseEntity.status(401).body(Map.of("error","Unauthorized","message","Invalid refresh"));
        }

        Member m = memberRepository.findByKakaoId(Long.valueOf(kakaoId))
                .orElse(null);
        if (m == null) {
            return ResponseEntity.status(401).body(Map.of("error","Unauthorized","message","Member not found"));
        }

        // ★ 서버(DB)의 현 버전으로 재발급 (토큰의 v와 달라도 상관 없음)
        String newAccess = tokenService.issueAccessToken(kakaoId, m.getTokenVersion());
        setCookie(res, "access_token", newAccess, accessTtlSeconds, secureCookie, SAME_SITE);

        return ResponseEntity.ok(Map.of("status","success"));
    }

    // 4) 로그아웃: (필터 적용) → DB 버전 +1 → 쿠키 삭제
    @PostMapping("/auth/logout")
    public ResponseEntity<?> logout(@CookieValue(name = "refresh_token", required = false) String refreshCookie,
                                    @CookieValue(name = "access_token", required = false) String accessCookie,
                                    HttpServletResponse res) {
        // 현재 로그인한 주체는 필터에서 SecurityContext에 들어있지만,
        // 여기서는 간단히 refresh에서 subject를 꺼내 조회해도 됨(쿠키-온리 전제)
        if (refreshCookie == null || !tokenService.validate(refreshCookie)) {
            // 그래도 버전만 올리면 과거 토큰 무효화 되므로, access로도 시도 가능(선택)
            clearCookie(res, "access_token", secureCookie, SAME_SITE);
            clearCookie(res, "refresh_token", secureCookie, SAME_SITE);
            return ResponseEntity.ok(Map.of("status","logged_out"));
        }

        String kakaoId = tokenService.getSubject(refreshCookie);
        Member m = (kakaoId == null) ? null :
                memberRepository.findByKakaoId(Long.valueOf(kakaoId)).orElse(null);

        if (m != null) {
            m.bumpTokenVersion();               // ★ 핵심: 버전 증가 = 모든 기존 토큰 즉시 무효
            memberRepository.save(m);
        }

        clearCookie(res, "access_token", secureCookie, SAME_SITE);
        clearCookie(res, "refresh_token", secureCookie, SAME_SITE);
        tokenService.revokeRefresh(refreshCookie); // 무상태(NOP)
        return ResponseEntity.ok(Map.of("status","logged_out"));
    }

    // ----------------- 내부 유틸 -----------------
    private static String buildAuthorizeUrl(String clientId, String redirectUri, String state) {
        StringBuilder sb = new StringBuilder("https://kauth.kakao.com/oauth/authorize")
                .append("?response_type=code")
                .append("&client_id=").append(url(clientId))
                .append("&redirect_uri=").append(url(redirectUri))
                // ✅ 이메일/닉네임/프로필 이미지 받기
                .append("&scope=").append(url("account_email profile_nickname"));
        // (선택) 매번 동의창 띄우고 싶으면 아래 주석 해제
        // .append("&prompt=consent")
        if (state != null && !state.isBlank()) {
            sb.append("&state=").append(url(state));
        }
        return sb.toString();
    }

    private static String url(String s) { return URLEncoder.encode(s, StandardCharsets.UTF_8); }

    private static void setCookie(HttpServletResponse res, String name, String value,
                                  int maxAgeSeconds, boolean secure, String sameSite) {
        Cookie c = new Cookie(name, value == null ? "" : value);
        c.setHttpOnly(true);
        c.setSecure(secure);
        c.setPath("/");
        c.setMaxAge(Math.max(maxAgeSeconds, 0));
        res.addCookie(c);

        StringBuilder header = new StringBuilder();
        header.append(name).append("=").append(value == null ? "" : value)
                .append("; Path=/; HttpOnly; SameSite=").append(sameSite);
        if (secure) header.append("; Secure");
        header.append("; Max-Age=").append(Math.max(maxAgeSeconds, 0));
        res.addHeader("Set-Cookie", header.toString());
    }

    private static void clearCookie(HttpServletResponse res, String name, boolean secure, String sameSite) {
        setCookie(res, name, "", 0, secure, sameSite);
    }
}
