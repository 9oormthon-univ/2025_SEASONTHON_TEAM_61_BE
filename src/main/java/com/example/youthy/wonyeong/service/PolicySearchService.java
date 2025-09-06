package com.example.youthy.wonyeong.service;

import com.example.youthy.YouthPolicy;
import com.example.youthy.YouthPolicyRepository;
import com.example.youthy.wonyeong.domain.PolicyFilter;
import com.example.youthy.wonyeong.domain.PolicySummaryDto;
import com.example.youthy.wonyeong.domain.PolicyFilter.RecruitStatus;
import com.example.youthy.wonyeong.repository.ZipcodeAreaRepository; // 경로 확인
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PolicyService {

    private final YouthPolicyRepository youthPolicyRepository;   // ✅ 기존 레포지토리 직접 사용
    private final ZipcodeAreaRepository zipcodeAreaRepository;   // ✅ 지역 → zip 목록 조회

    public Page<PolicySummaryDto> search(PolicyFilter filter, Pageable pageable) {
        Specification<YouthPolicy> spec = WonyeongPolicySpecs.withFilter(filter, zipcodeAreaRepository);
        Page<YouthPolicy> page = youthPolicyRepository.findAll(spec, pageable);

        // 모집현황 후처리(문자열 날짜일 경우)
        List<PolicySummaryDto> content = page.getContent().stream()
                .map(PolicySummaryDto::from)
                .filter(dto -> matchStatus(filter.status(), dto.recruitStatus()))
                .toList();

        return new PageImpl<>(content, pageable, page.getTotalElements());
    }

    private boolean matchStatus(RecruitStatus want, String actual) {
        if (want == null) return true;
        return switch (want) {
            case OPEN   -> "OPEN".equalsIgnoreCase(actual);
            case CLOSED -> "CLOSED".equalsIgnoreCase(actual);
            case ALWAYS -> "ALWAYS".equalsIgnoreCase(actual);
        };
    }
}
