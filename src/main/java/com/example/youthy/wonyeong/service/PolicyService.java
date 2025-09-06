package com.example.youthy.wonyeong.service;

import com.example.youthy.wonyeong.domain.Policy;
import com.example.youthy.wonyeong.domain.PolicyRepository;
import com.example.youthy.wonyeong.domain.Sigungu;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PolicyService {
    private final PolicyRepository policyRepository;

    public List<Policy> findPoliciesBySigungu(Sigungu sigungu) {
        return policyRepository.findBySigungu(sigungu);
    }
}
