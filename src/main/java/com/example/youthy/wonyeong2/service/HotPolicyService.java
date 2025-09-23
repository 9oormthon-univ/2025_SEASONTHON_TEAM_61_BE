package com.example.youthy.wonyeong2.service;

import com.example.youthy.YouthPolicy;
import com.example.youthy.YouthPolicyRepository;
import com.example.youthy.wonyeong2.dto.HotPolicyDto;
import com.example.youthy.wonyeong2.repository.HotPolicyQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * Redis Sorted Set을 활용하여 실시간 인기 정책을 조회하는 서비스
 */
@Service
@RequiredArgsConstructor
public class HotPolicyService {

    private final YouthPolicyRepository youthPolicyRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private static final String POPULAR_POLICIES_KEY = "popular_policies";

    @Transactional(readOnly = true)
    public List<HotPolicyDto> getHotPolicies(int limit) {
        // 1. Redis Sorted Set에서 점수가 높은 순(내림차순)으로 policyNo를 N개 가져옵니다.
        Set<String> topPolicyNos = redisTemplate.opsForZSet()
                .reverseRange(POPULAR_POLICIES_KEY, 0, limit - 1);

        if (topPolicyNos == null || topPolicyNos.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. 가져온 policyNo 목록으로 DB에서 정책 상세 정보를 한 번에 조회합니다.
        List<YouthPolicy> policies = youthPolicyRepository.findAllById(topPolicyNos);

        // 3. 조회된 정책들을 HotPolicyDto 리스트로 변환하고, 조회수 높은 순으로 다시 정렬합니다.
        // (findAllById는 순서를 보장하지 않으므로, 애플리케이션에서 재정렬이 필요합니다.)
        return policies.stream()
                .map(p -> HotPolicyDto.builder()
                        .policyNo(p.getPolicyNo())
                        .policyName(p.getPolicyName())
                        .viewCount(p.getViewCount())
                        .build())
                .sorted((p1, p2) -> Integer.compare(p2.getViewCount(), p1.getViewCount()))
                .collect(Collectors.toList());
    }
}

