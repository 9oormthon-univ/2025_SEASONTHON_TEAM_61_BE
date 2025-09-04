package com.example.youthy.service;

import com.example.youthy.domain.Member;
import com.example.youthy.dto.Tokens;

public interface TokenService {
    // JWT 발급 (버전 클레임 포함)
    String issueAccessToken(String kakaoId, int tokenVersion);
    String issueRefreshToken(String kakaoId, int tokenVersion);

    // 공용 파서
    boolean validate(String jwt);
    String getSubject(String jwt);   // kakaoId
    Integer getVersion(String jwt);  // v (tokenVersion)

    // 무상태 전략: 저장/블랙리스트 미사용
    void revokeRefresh(String refreshJwt);

    // ✅ 기존 코드 호환용 메서드 (컨트롤러/서비스에서 호출하던 것)
    Tokens generateTokens(Member member);
}
