// src/main/java/com/example/youthy/service/KakaoService.java
package com.example.youthy.service;

import com.example.youthy.domain.Member;
import com.example.youthy.dto.Tokens;
import com.example.youthy.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class KakaoService {

    private final MemberRepository memberRepository;
    private final TokenService tokenService;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${kakao.client.id}")
    private String clientId;

    @Value("${kakao.client.secret:}")
    private String clientSecret;

    /**
     * 허용된 프론트 오리진(화이트리스트).
     * 예: "http://localhost:3000,https://your-frontend.com"
     */
    @Value("#{'${kakao.allowed-redirect-origins:}'.empty ? null : '${kakao.allowed-redirect-origins:}'.split(',')}")
    private List<String> allowedRedirectOrigins;

    // ========= 1) redirectUri 화이트리스트 검사 =========
    public void validateRedirectOrigin(String redirectUri) {
        try {
            URI u = URI.create(redirectUri);
            String origin = u.getScheme() + "://" + u.getHost() + (u.getPort() > 0 ? ":" + u.getPort() : "");
            if (allowedRedirectOrigins == null || allowedRedirectOrigins.isEmpty()) {
                // 개발 편의상 통과 — 운영에서는 반드시 설정 권장
                return;
            }
            boolean ok = allowedRedirectOrigins.stream()
                    .map(String::trim)
                    .anyMatch(o -> o.equalsIgnoreCase(origin));
            if (!ok) throw new IllegalArgumentException("redirectUri not allowed: " + origin);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid redirectUri", e);
        }
    }

    // ========= 2) 인가코드 → 카카오 access_token 교환 =========
    public String getAccessTokenWithRedirect(String code, String redirectUri) {
        String tokenUrl = "https://kauth.kakao.com/oauth/token";

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "authorization_code");
        form.add("client_id", clientId);
        if (clientSecret != null && !clientSecret.isBlank()) {
            form.add("client_secret", clientSecret);
        }
        form.add("code", code);
        // ★ 인가요청 때 사용한 redirect_uri와 "완전히 동일"해야 함
        form.add("redirect_uri", redirectUri);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        try {
            ResponseEntity<Map<String, Object>> resp = restTemplate.exchange(
                    tokenUrl, HttpMethod.POST, new HttpEntity<>(form, headers),
                    new ParameterizedTypeReference<>() {}
            );
            Map<String, Object> body = resp.getBody();
            if (body == null || body.get("access_token") == null) {
                throw new IllegalStateException("No access_token from Kakao");
            }
            return String.valueOf(body.get("access_token"));
        } catch (HttpStatusCodeException e) {
            throw new RuntimeException("Kakao token error: " + e.getStatusCode() + " " + e.getResponseBodyAsString(), e);
        }
    }

    // ========= 3) 카카오 유저 정보 조회 =========
    public Map<String, Object> getUserInfo(String kakaoAccessToken) {
        String userInfoUrl = "https://kapi.kakao.com/v2/user/me";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(kakaoAccessToken);

        try {
            ResponseEntity<Map<String, Object>> resp = restTemplate.exchange(
                    userInfoUrl, HttpMethod.GET, new HttpEntity<>(headers),
                    new ParameterizedTypeReference<>() {}
            );
            if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
                throw new IllegalStateException("Fail to get user info from Kakao");
            }
            return resp.getBody();
        } catch (HttpStatusCodeException e) {
            throw new RuntimeException("Kakao userinfo error: " + e.getStatusCode() + " " + e.getResponseBodyAsString(), e);
        }
    }

    // ========= 4) 유저 upsert + 우리 토큰 발급 =========
    // TokenService의 실제 메서드 시그니처에 맞춤 (rotateAndIssue / mintRefresh 사용 가능)
    public Tokens processUser(Map<String, Object> userInfo, String userAgent, String ip) {
        Long kakaoId = Long.valueOf(String.valueOf(userInfo.get("id")));
        @SuppressWarnings("unchecked")
        Map<String, Object> kakaoAccount = (Map<String, Object>) userInfo.get("kakao_account");
        @SuppressWarnings("unchecked")
        Map<String, Object> properties = (Map<String, Object>) userInfo.get("properties");

        String email = kakaoAccount != null ? (String) kakaoAccount.get("email") : null;
        String nickname = properties != null ? (String) properties.get("nickname") : null;

        Member member = memberRepository.findByKakaoId(kakaoId)
                .map(m -> {
                    boolean dirty = false;
                    if (nickname != null && !nickname.equals(m.getUsername())) { m.setUsername(nickname); dirty = true; }
                    if (email != null && !email.equals(m.getEmail()))       { m.setEmail(email);       dirty = true; }
                    return dirty ? memberRepository.save(m) : m;
                })
                .orElseGet(() -> {
                    Member m = new Member();
                    m.setKakaoId(kakaoId);
                    m.setEmail(email);
                    m.setUsername(nickname);
                    return memberRepository.save(m);
                });

        // A) 회전 규칙까지 TokenService에 위임(권장)
        var pair = tokenService.rotateAndIssue(member, null, userAgent, ip);
        return new Tokens(pair.getAccess(), pair.getRefresh());

        // B) 단순 발급 원할 경우(위 return 주석 처리하고 아래 사용)
        // String access = tokenService.createAccess(member, 0);
        // String refresh = tokenService.mintRefresh(member, userAgent, ip);
        // return new Tokens(access, refresh);
    }

    // 기존 호환 (UA/IP가 필요 없을 때)
    public Tokens processUser(Map<String, Object> userInfo) {
        return processUser(userInfo, null, null);
    }
}
