package com.example.youthy.chungheon2;

import com.example.youthy.YouthPolicy;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * PolicySearchCondition 객체를 기반으로 동적인 JPA Specification을 생성하는 클래스입니다.
 */
public class YouthPolicySpecification {

    /**
     * 검색 조건 객체를 받아 최종 Specification을 반환합니다.
     * @param condition 사용자가 입력한 검색 조건
     * @return JPA Specification 객체
     */
    public static Specification<YouthPolicy> from(PolicySearchCondition condition) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 카테고리 조건: category 값이 존재하면 WHERE 절에 추가
            if (StringUtils.hasText(condition.getCategory())) {
                predicates.add(criteriaBuilder.equal(root.get("policyField"), condition.getCategory()));
            }

            // 키워드 조건: keyword 값이 존재하면 WHERE 절에 추가 (정책 이름에서 like 검색)
            if (StringUtils.hasText(condition.getKeyword())) {
                predicates.add(criteriaBuilder.like(root.get("policyName"), "%" + condition.getKeyword() + "%"));
            }

            // 모든 조건을 AND로 결합하여 반환
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}

