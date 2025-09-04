package com.example.youthy.service;

import com.example.youthy.domain.Member;
import com.example.youthy.dto.Tokens;
import com.example.youthy.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class KakaoService {

    // ===== application.yml 과 매핑되는 설정 값들 =====

    // kakao.client.id
    @Value("${kakao.client.id}")
    private String clientId;

    // kakao.client.secret (선택)
    @Value("${kakao.client.secret:}")
    private String clientSecret;

    // kakao.login.redirect
    @Value("${kakao.login.redirect}")
    private String redirectUri;

    // kakao.logout.redirect
    @Value("${kakao.logout.redirect}")
    private String logoutRedirect;

    // app.front-url (선택: 프론트엔드 리다이렉트 등에 활용)
    @Value("${app.front-url}")
    private String frontUrl;

    // ==============================================

    private final MemberRepository memberRepository;
    private final TokenService tokenService;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * ✅ 컨트롤러에서 사용하는 메서드
     * 카카오 인가 코드로 사용자 kakaoId(Long) 반환
     * - 1) 인가코드 → 토큰 교환
     * - 2) access_token 으로 사용자 정보 조회
     * - 3) user.id 반환 (없으면 null)
     */
    public Long exchangeCodeForKakaoId(String authorizationCode) {
        Map<String, Object> token = exchangeCodeForToken(authorizationCode);
        if (token == null || token.get("access_token") == null) {
            return null;
        }
        String accessToken = (String) token.get("access_token");

        Map<String, Object> userInfo = getUserInfo(accessToken);
        if (userInfo == null || userInfo.get("id") == null) {
            return null;
        }
        return ((Number) userInfo.get("id")).longValue();
    }

    /**
     * 카카오 인가 코드로 토큰 교환
     */
    public Map<String, Object> exchangeCodeForToken(String authorizationCode) {
        String tokenUrl = "https://kauth.kakao.com/oauth/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAcceptCharset(java.util.Collections.singletonList(StandardCharsets.UTF_8));

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        if (clientSecret != null && !clientSecret.isBlank()) {
            body.add("client_secret", clientSecret);
        }
        body.add("redirect_uri", redirectUri);
        body.add("code", authorizationCode);

        HttpEntity<MultiValueMap<String, String>> req = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> resp = restTemplate.exchange(
                    tokenUrl,
                    HttpMethod.POST,
                    req,
                    Map.class
            );
            return resp.getBody();
        } catch (HttpStatusCodeException e) {
            throw new RuntimeException("Kakao token error: " + e.getResponseBodyAsString(), e);
        }
    }

    /**
     * 카카오 사용자 정보 조회
     */
    public Map<String, Object> getUserInfo(String accessToken) {
        String userUrl = "https://kapi.kakao.com/v2/user/me";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<Void> req = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> resp = restTemplate.exchange(
                    userUrl,
                    HttpMethod.POST, // Kakao는 GET도 가능하지만 기존 코드 유지
                    req,
                    Map.class
            );
            return resp.getBody();
        } catch (HttpStatusCodeException e) {
            throw new RuntimeException("Kakao user info error: " + e.getResponseBodyAsString(), e);
        }
    }

    /**
     * 카카오 로그인 → DB 저장/조회 → JWT 발급(버전 클레임 포함)
     * - 기존 generateTokens(member) 의존 제거
     * - TokenService.issueAccessToken/issueRefreshToken 사용
     */
    @SuppressWarnings("unchecked")
    public Tokens loginOrSignup(Map<String, Object> userInfo) {
        // 카카오 ID 추출 (필수)
        Long kakaoId = ((Number) userInfo.get("id")).longValue();

        // 카카오 계정 정보
        Map<String, Object> kakaoAccount = (Map<String, Object>) userInfo.get("kakao_account");
        String nickname = null;
        String email = null;
        if (kakaoAccount != null) {
            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
            if (profile != null) {
                nickname = (String) profile.get("nickname"); // 동의 필요
            }
            email = (String) kakaoAccount.get("email");      // 동의 필요
        }

        // (선택) 원문 로그로 실제 들어오는지 즉시 확인
        // log.info("Kakao userInfo: {}", userInfo);
        // log.info("Parsed nickname={}, email={}", nickname, email);

        // DB 조회 또는 신규 생성
        Optional<Member> existing = memberRepository.findByKakaoId(kakaoId);
        Member member;
        if (existing.isPresent()) {
            member = existing.get();

            // ✅ 기존 회원도 최신 프로필로 갱신 (null 아닌 값만 반영)
            boolean changed = false;
            if (nickname != null && !nickname.equals(member.getUsername())) {
                member.setUsername(nickname);
                changed = true;
            }
            if (email != null && !email.equals(member.getEmail())) {
                member.setEmail(email);
                changed = true;
            }
            if (changed) {
                memberRepository.save(member);
            }
        } else {
            member = new Member();
            member.setKakaoId(kakaoId);
            // 닉네임 없으면 기본값
            member.setUsername(nickname != null ? nickname : "user_" + kakaoId);
            member.setEmail(email); // null 허용 (동의 안 한 경우)
            memberRepository.save(member);
        }

        // ✅ 버전 클레임 포함하여 JWT 직접 발급 (기존 로직 유지)
        int v = member.getTokenVersion();
        String subject = String.valueOf(member.getKakaoId());
        String access  = tokenService.issueAccessToken(subject, v);
        String refresh = tokenService.issueRefreshToken(subject, v);

        return new Tokens(access, refresh);
    }

    /**
     * 카카오 로그아웃 (프론트 URL 리다이렉트 시 활용 가능)
     */
    public String getLogoutRedirectUrl() {
        return logoutRedirect;
    }

    /**
     * 프론트엔드 URL 반환 (선택적 사용)
     */
    public String getFrontUrl() {
        return frontUrl;
    }
}
