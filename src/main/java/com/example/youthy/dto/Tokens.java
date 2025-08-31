package com.example.youthy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Access/Refresh 페어.
 * - 신규: getAccess(), getRefresh()
 * - 호환: getAccessToken(), getRefreshToken() 도 지원
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tokens {
    private String access;
    private String refresh;

    // === 호환 게터 (기존 코드가 사용하는 이름) ===
    public String getAccessToken() { return access; }
    public String getRefreshToken() { return refresh; }
}
