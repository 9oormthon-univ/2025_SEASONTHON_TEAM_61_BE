package com.example.youthy.chungheon2;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
        // 1. 정렬 규칙을 동적으로 생성합니다.
        Sort sort = createSort(condition.getSort());

        // 2. 기존 페이징 정보에 새로운 정렬 규칙을 적용한 새 Pageable 객체를 만듭니다.
        Pageable customPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        // 3. 정렬 규칙이 적용된 Pageable을 사용하여 서비스를 호출합니다.
        Page<PolicyCategoryDto> results = youthPolicyService.searchPolicies(condition, customPageable);
        return ResponseEntity.ok(results);
    }

    /**
     * 정렬 옵션 문자열을 기반으로 Sort 객체를 생성하는 헬퍼 메서드입니다.
     * @param sortOption "latest"(최신순) 또는 "soon"(마감순)
     * @return Sort 객체
     */
    private Sort createSort(String sortOption) {
        if ("soon".equalsIgnoreCase(sortOption)) {
            // "마감순": applicationEndDate(종료일) 기준 오름차순 정렬 (마감 임박순)
            // 종료일이 없는(null) 데이터는 가장 마지막에 표시됩니다.
            return Sort.by(Sort.Direction.ASC, "applicationEndDate");
        }

        // 기본값 및 "최신순": applicationStartDate(시작일) 기준 오름차순 정렬 (시작일이 오래된 순)
        // 시작일이 없는(null) 데이터는 가장 마지막에 표시됩니다.
        return Sort.by(Sort.Direction.DESC, "applicationStartDate");
    }
}
