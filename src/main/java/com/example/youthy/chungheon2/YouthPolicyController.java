package com.example.youthy.chungheon2;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault; // PageableDefault import
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/policies")
public class YouthPolicyController {

    private final YouthPolicyService youthPolicyService;

    // ... (기존 searchPolicies, getPolicyDetail 엔드포인트는 그대로 유지)

    /**
     * 특정 카테고리의 정책 목록을 12개씩 페이징하여 조회합니다.
     * @param category 조회할 정책 카테고리 (예: 일자리, 복지문화)
     * @param pageable 페이징 정보 (클라이언트에서 size 미지정 시 12개)
     * @return 페이징된 정책 목록 (정책이름, 카테고리, 디데이, 정책번호)
     */
    @GetMapping // "/category/{category}" 부분을 제거하여 기본 경로로 변경
    public ResponseEntity<Page<PolicyCategoryDto>> getPolicies(
            @RequestParam(required = false) String category, // PathVariable을 RequestParam으로 변경
            @PageableDefault(size = 12) Pageable pageable) {

        Page<PolicyCategoryDto> results;

        // ✅ category 파라미터의 존재 여부에 따라 다른 서비스 메서드를 호출
        if (StringUtils.hasText(category)) {
            // 카테고리 값이 있으면: 카테고리별 조회
            results = youthPolicyService.findPoliciesByCategory(category, pageable);
        } else {
            // 카테고리 값이 없으면: 전체 조회
            results = youthPolicyService.findAllPolicies(pageable);
        }

        return ResponseEntity.ok(results);
    }
}

