package com.example.youthy.wonyeong.service;

import com.example.youthy.PolicyResidence;
import com.example.youthy.YouthPolicy;
import com.example.youthy.domain.ZipcodeArea;
import com.example.youthy.wonyeong.domain.PolicyFilter;
import com.example.youthy.wonyeong.domain.Sido;
import com.example.youthy.wonyeong.domain.Sigungu;
import com.example.youthy.wonyeong.repository.ZipcodeAreaRepository;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class WonyeongPolicySpecs {

    private WonyeongPolicySpecs() {}

    public static Specification<YouthPolicy> withFilter(
            PolicyFilter f,
            ZipcodeAreaRepository zipcodeAreaRepository
    ) {
        return (root, cq, cb) -> {
            List<Predicate> preds = new ArrayList<>();

            // 1) 카테고리(다중)
            if (f.categories() != null && !f.categories().isEmpty()) {
                preds.add(root.get("policyField").in(f.categories()));
            }

            // 2) 지역 → residences.zipCode IN (구성 zip 목록)
            if (f.sido() != null || f.sigungu() != null) {
                List<String> zips = resolveZipcodes(f.sido(), f.sigungu(), zipcodeAreaRepository);
                if (!zips.isEmpty()) {
                    Join<YouthPolicy, PolicyResidence> res = root.join("residences", JoinType.INNER);
                    preds.add(res.get("zipCode").in(zips));
                }
            }

            // 3) 담당기관 LIKE  (⚠️ YouthPolicy 실제 필드명 확인 필요)
            if (f.agency() != null && !f.agency().isBlank()) {
                String like = "%" + f.agency().trim() + "%";
                preds.add(cb.like(root.get("operatingAgency"), like));
            }

            // 4) 대상(키워드) - 학력/전공/취업상태/특화분야 OR 매칭 (⚠️ 실제 필드명 확인)
            if (f.target() != null && !f.target().isBlank()) {
                String like = "%" + f.target().trim() + "%";
                preds.add(cb.or(
                        cb.like(root.get("educationRequirement"), like),
                        cb.like(root.get("majorRequirement"), like),
                        cb.like(root.get("employmentStatus"), like),
                        cb.like(root.get("specializedField"), like)
                ));
            }

            // 5) 모집현황(OPEN/CLOSED/ALWAYS)은 문자열 날짜 컬럼일 수 있어 DB단 비교 어렵습니다 → 서비스단에서 후처리

            return preds.isEmpty() ? cb.conjunction()
                    : cb.and(preds.toArray(new Predicate[0]));
        };
    }

    private static List<String> resolveZipcodes(Sido sido, Sigungu sigungu, ZipcodeAreaRepository repo) {
        if (sido == null && sigungu == null) return List.of();

        String region = (sido != null) ? sido.getNameKo() : null;       // ex) "서울특별시"
        String sub    = (sigungu != null) ? sigungu.getNameKo() : null; // ex) "노원구"

        if (region != null && sub != null) {
            return repo.findAllByRegionAndSubregion(region, sub).stream()
                    .map(ZipcodeArea::getZipCode).toList();
        }
        if (region != null) {
            return repo.findAllByRegion(region).stream()
                    .map(ZipcodeArea::getZipCode).toList();
        }
        return List.of();
    }
}
