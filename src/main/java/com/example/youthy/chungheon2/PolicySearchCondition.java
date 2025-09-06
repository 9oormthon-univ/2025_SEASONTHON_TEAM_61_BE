package com.example.youthy.chungheon2;

import lombok.Getter;
import lombok.Setter;

/**
 * 정책 검색 조건을 담는 DTO 입니다.
 * 클라이언트로부터 받은 category, keyword 등의 파라미터를 객체로 바인딩합니다.
 * 예: /api/v1/policies?category=일자리&keyword=지원금
 */
@Getter
@Setter
public class PolicySearchCondition {

    /**
     * 검색할 정책 카테고리 (policyField)
     */
    private String category;

    /**
     * 검색할 키워드 (policyName)
     */
    private String keyword;

    // 필요에 따라 다른 검색 조건(지역, 연령 등)을 여기에 추가할 수 있습니다.
}

