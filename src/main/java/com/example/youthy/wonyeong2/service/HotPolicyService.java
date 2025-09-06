package com.example.youthy.wonyeong2.service;

import com.example.youthy.YouthPolicy;
import com.example.youthy.wonyeong2.dto.HotPolicyDto;
import com.example.youthy.wonyeong2.repository.HotPolicyQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HotPolicyService {

    private final HotPolicyQueryRepository repository;

    private final AtomicReference<List<HotPolicyDto>> cache = new AtomicReference<>(List.of());
    private volatile Instant lastRefreshedAt = Instant.EPOCH;

    private static final int DEFAULT_LIMIT = 10;

    @Scheduled(cron = "0 0 * * * *")
    public void hourlyRefresh() {
        refreshCache(DEFAULT_LIMIT);
    }

    @Transactional(readOnly = true)
    public synchronized void refreshCache(int limit) {
        List<YouthPolicy> top = repository.findAllByOrderByViewCountDesc(PageRequest.of(0, limit));

        List<HotPolicyDto> hot = top.stream()
                .map(p -> HotPolicyDto.builder()
                        .policyNo(p.getPolicyNo())       // <-- 수정
                        .policyName(p.getPolicyName())   // <-- 수정
                        .viewCount(p.getViewCount())
                        .build())
                .collect(Collectors.toList());           // 타입 추론 정상화

        cache.set(hot);
        lastRefreshedAt = Instant.now();
    }

    public List<HotPolicyDto> getHot(int limit) {
        List<HotPolicyDto> current = cache.get();
        if (limit > 0 && limit < current.size()) {
            return current.subList(0, limit);
        }
        return current;
    }

    public Instant getLastRefreshedAt() {
        return lastRefreshedAt;
    }
}
