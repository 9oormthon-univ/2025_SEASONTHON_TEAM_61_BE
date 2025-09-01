// src/main/java/com/example/youthy/service/KakaoService.java
package com.example.youthy.service;

import com.example.youthy.domain.Member;
import com.example.youthy.repository.MemberRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class KakaoService {

    @Value("${kakao.client.id}")   private String clientId;
    @Value("${kakao.client.secret:}") private String clientSecret; // 없으면 빈 문자열
    @Value("${kakao.login.redirect}") private String redirectUri;
    @Value("${kakao.logout.redirect}") private String logoutRedirect;
    @Value("${jwt.secret}") private String jwtSecret;

    private final RestTemplate restTemplate = new RestTemplate();
    private final MemberRepository memberRepository;
    private final TokenService tokenService;

    public String getAccessToken(String code) {
        String url = "https://kauth.kakao.com/oauth/token";

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId.trim());
        params.add("redirect_uri", redirectUri.trim());
        params.add("code", code.trim());
        if (!clientSecret.isBlank()) {
            params.add("client_secret", clientSecret.trim());
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf("application/x-www-form-urlencoded;charset=UTF-8"));
        headers.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<Map<String, Object>> res = restTemplate.exchange(
                    url, HttpMethod.POST, request, new ParameterizedTypeReference<Map<String, Object>>() {}
            );
            Map<String, Object> body = res.getBody();
            if (body == null || !body.containsKey("access_token")) {
                throw new IllegalStateException("No access_token in response: " + body);
            }
            return (String) body.get("access_token");
        } catch (HttpStatusCodeException e) {
            throw new IllegalStateException("Kakao token error: " + e.getStatusCode() + " " + e.getResponseBodyAsString(), e);
        }
    }

    public Map<String, Object> getUserInfo(String accessToken) {
        String url = "https://kapi.kakao.com/v2/user/me";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        ResponseEntity<Map<String, Object>> res = restTemplate.exchange(
                url, HttpMethod.GET, new HttpEntity<>(headers),
                new ParameterizedTypeReference<Map<String, Object>>() {}
        );
        return res.getBody();
    }

    // ✅ kakaoId 기준으로 업서트하고 JWT 생성
    public com.example.youthy.dto.Tokens processUser(Map<String, Object> userInfo) {
        Long kakaoId = Long.valueOf(userInfo.get("id").toString());
        Map<String, Object> kakaoAccount = (Map<String, Object>) userInfo.get("kakao_account");
        Map<String, Object> properties   = (Map<String, Object>) userInfo.get("properties");

        String email = kakaoAccount != null ? (String) kakaoAccount.get("email") : null;
        String nickname = properties != null ? (String) properties.get("nickname") : null;

        Member member = memberRepository.findByKakaoId(kakaoId)
                .map(m -> {
                    if (nickname != null && !nickname.equals(m.getUsername())) m.setUsername(nickname);
                    if (email != null && (m.getEmail() == null || !email.equals(m.getEmail()))) m.setEmail(email);
                    return m;
                })
                .orElseGet(() -> Member.builder()
                        .kakaoId(kakaoId)
                        .email(email)
                        .username(nickname)
                        .build());

        memberRepository.save(member);

        // 🔧 여기 수정: TokenService 최신 시그니처에 맞춤
        String access = tokenService.createAccessToken(member);
        String refresh = tokenService.issueRefreshToken(member, null, null); // UA/IP 없으면 null로 OK

        return new com.example.youthy.dto.Tokens(access, refresh);
    }

    // (현재는 미사용) 직접 JWT 만들고 싶을 때 예시
    private String createJwtToken(Member member) {
        long now = System.currentTimeMillis();
        long validityMs = 1000L * 60 * 60; // 1시간

        return Jwts.builder()
                .setSubject(String.valueOf(member.getKakaoId()))
                .claim("id", member.getId())
                .claim("kakaoId", member.getKakaoId())
                .claim("username", member.getUsername())
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + validityMs))
                .signWith(SignatureAlgorithm.HS256, jwtSecret.getBytes())
                .compact();
    }

    public String buildKakaoLogoutUrl() {
        return "https://kauth.kakao.com/oauth/logout?client_id=" + clientId + "&logout_redirect_uri=" + logoutRedirect;
    }
}
