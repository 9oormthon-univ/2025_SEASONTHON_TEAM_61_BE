package com.example.youthy.chungheon2;

import com.example.youthy.YouthPolicy;
import com.example.youthy.YouthPolicyRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.service.spi.ServiceException;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class YouthPolicyService {

    private final YouthPolicyRepository youthPolicyRepository;
    private static final String POPULAR_POLICIES_KEY = "popular_policies";

    private final RedisTemplate<String, String> redisTemplate;


    /**
     * 다양한 검색 조건에 따라 정책 목록을 동적으로 조회합니다.
     * @param condition 검색 조건(카테고리, 키워드 등)
     * @param pageable 페이징 정보
     * @return 페이징된 정책 DTO 목록
     */
    @Transactional(readOnly = true)
    @Cacheable(
            value = "policies", // 캐시를 저장할 그룹 이름
            key = "T(java.util.Objects).hash(#condition.category, #condition.keyword, #pageable.pageNumber, #pageable.sort)", // 캐시 키 생성
            unless = "#result == null or #result.empty" // 결과가 비어있으면 캐시하지 않음
    )
    public Page<PolicyCategoryDto> searchPolicies(PolicySearchCondition condition, Pageable pageable) {
        // 검색 조건으로 Specification 객체 생성
        Specification<YouthPolicy> spec = YouthPolicySpecification.from(condition);

        // Specification을 사용하여 DB에서 조건에 맞는 데이터 조회
        Page<YouthPolicy> entities = youthPolicyRepository.findAll(spec, pageable);

        // Page<YouthPolicy>를 Page<PolicyCategoryDto>로 변환하여 반환
        return entities.map(PolicyCategoryDto::new);
    }

    /**
     * 정책 번호(ID)로 특정 정책의 상세 정보를 조회합니다.
     * @param policyNo 조회할 정책의 고유 번호
     * @return 정책 상세 정보 DTO
     */
    @Transactional
    public PolicyDetailDto getPolicyDetail(String policyNo) {
        YouthPolicy policy = youthPolicyRepository.findById(policyNo)
                .orElseThrow(() -> new ServiceException("정책을 찾을 수 없습니다. policyNo=" + policyNo));

        // ✅ 1. [영구 저장소] RDS(MySQL)의 view_count 컬럼 1 증가
        policy.increaseViewCount();

        // ✅ 2. [실시간 랭킹] Redis Sorted Set의 점수(score) 1 증가
        redisTemplate.opsForZSet().incrementScore(POPULAR_POLICIES_KEY, policy.getPolicyNo(), 1);

        return new PolicyDetailDto(policy);
    }
}