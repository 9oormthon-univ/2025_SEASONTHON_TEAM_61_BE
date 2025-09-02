// src/main/java/com/example/youthy/service/KakaoService.java
package com.example.youthy.service;

import com.example.youthy.domain.Member;
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

import java.util.Map;

@Service
@RequiredArgsConstructor
public class KakaoService {

    @Value("${kakao.client.id}")        private String clientId;          // REST API Key
    @Value("${kakao.client.secret:}")   private String clientSecret;      // 콘솔에서 '사용함'이면 반드시 전송
    @Value("${kakao.login.redirect}")   private String redirectUri;
    @Value("${kakao.logout.redirect}")  private String logoutRedirect;

    private final RestTemplate restTemplate = new RestTemplate();
    private final MemberRepository memberRepository;
    private final TokenService tokenService;

    /** 인가코드로 카카오 access_token 교환 */
    public String getAccessToken(String code) {
        String url = "https://kauth.kakao.com/oauth/token";

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId.trim());
        params.add("redirect_uri", redirectUri.trim());
        params.add("code", code.trim());
        // 콘솔에서 Client Secret '사용함'이면 필수
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
            return String.valueOf(body.get("access_token"));
        } catch (HttpStatusCodeException e) {
            throw new IllegalStateException("Kakao token error: " + e.getStatusCode() + " " + e.getResponseBodyAsString(), e);
        }
    }

    /** 카카오 사용자 정보 조회 */
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

    /** kakaoId 기준 업서트 후 우리 서비스 토큰(Access/Refresh) 발급 */
    public com.example.youthy.dto.Tokens processUser(Map<String, Object> userInfo) {
        Long kakaoId = Long.valueOf(String.valueOf(userInfo.get("id")));
        @SuppressWarnings("unchecked")
        Map<String, Object> kakaoAccount = (Map<String, Object>) userInfo.get("kakao_account");
        @SuppressWarnings("unchecked")
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

        // Access(기본 TTL 사용) + Refresh 발급
        String access  = tokenService.createAccess(member, 0);
        String refresh = tokenService.mintRefresh(member, null, null); // UA/IP 필요시 Web 레이어에서 주입

        return new com.example.youthy.dto.Tokens(access, refresh);
    }

    /** 카카오 계정 로그아웃 URL 생성(옵션) */
    public String buildKakaoLogoutUrl() {
        return "https://kauth.kakao.com/oauth/logout?client_id=" + clientId + "&logout_redirect_uri=" + logoutRedirect;
    }

    // (미사용 예시) 직접 JWT 생성 메서드는 현재 TokenService 사용으로 대체됨.
    // 필요하면 삭제해도 무방.
}
