package com.example.youthy.chungheon2;

import com.example.youthy.YouthPolicy;
import com.example.youthy.YouthPolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class YouthPolicyService {

    private final YouthPolicyRepository youthPolicyRepository;

    // ... (기존 searchPolicies, getPolicyDetail 메서드는 그대로 유지)

    /**
     * 특정 카테고리에 해당하는 정책 목록을 조회합니다.
     * @param category 조회할 정책 카테고리
     * @param pageable 페이징 정보
     * @return 페이징된 정책 카테고리 DTO 목록
     */
    public Page<PolicyCategoryDto> findPoliciesByCategory(String category, Pageable pageable) {
        Page<YouthPolicy> entities = youthPolicyRepository.findByPolicyField(category, pageable);

        // Page<YouthPolicy>를 Page<PolicyCategoryDto>로 변환하여 반환
        return entities.map(PolicyCategoryDto::new);
    }
}

