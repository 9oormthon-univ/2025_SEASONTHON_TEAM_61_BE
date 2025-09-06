package com.example.youthy.chungheon2;

import com.example.youthy.YouthPolicy;
import com.example.youthy.YouthPolicyRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.service.spi.ServiceException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class YouthPolicyService {

    private final YouthPolicyRepository youthPolicyRepository;

    /**
     * 다양한 검색 조건에 따라 정책 목록을 동적으로 조회합니다.
     * @param condition 검색 조건(카테고리, 키워드 등)
     * @param pageable 페이징 정보
     * @return 페이징된 정책 DTO 목록
     */
    public Page<PolicyCategoryDto> searchPolicies(PolicySearchCondition condition, Pageable pageable) {
        // 검색 조건으로 Specification 객체 생성
        Specification<YouthPolicy> spec = YouthPolicySpecification.from(condition);

        // Specification을 사용하여 DB에서 조건에 맞는 데이터 조회
        Page<YouthPolicy> entities = youthPolicyRepository.findAll(spec, pageable);

        // Page<YouthPolicy>를 Page<PolicyCategoryDto>로 변환하여 반환
        return entities.map(PolicyCategoryDto::new);
    }
}