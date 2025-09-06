package com.example.youthy.chungheon2;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/policies")
public class YouthPolicyController {

    private final YouthPolicyService youthPolicyService;

    // ... (기존 getPolicyDetail 엔드포인트는 그대로 유지)

    /**
     * 카테고리 및 검색어 등 다양한 조건에 따라 정책 목록을 12개씩 페이징하여 조회합니다.
     * 모든 파라미터는 선택 사항이며, 없을 경우 전체 목록이 조회됩니다.
     * @param condition 검색 조건(category, keyword 등)을 담는 DTO
     * @param pageable 페이징 정보
     * @return 페이징된 정책 목록
     */
    @GetMapping
    public ResponseEntity<Page<PolicyCategoryDto>> searchPolicies(
            @ModelAttribute PolicySearchCondition condition,
            @PageableDefault(size = 12) Pageable pageable) {

        Page<PolicyCategoryDto> results = youthPolicyService.searchPolicies(condition, pageable);
        return ResponseEntity.ok(results);
    }
}
