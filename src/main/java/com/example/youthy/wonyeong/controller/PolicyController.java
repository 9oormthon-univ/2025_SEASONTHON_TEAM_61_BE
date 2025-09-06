package com.example.youthy.wonyeong.controller;

import com.example.youthy.wonyeong.domain.Policy;
import com.example.youthy.wonyeong.domain.Sigungu;
import com.example.youthy.wonyeong.service.PolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/policies")
@RequiredArgsConstructor
public class PolicyController {

    private final PolicyService policyService;

    @GetMapping("/by-sigungu")
    public List<Policy> getBySigungu(@RequestParam String sigunguName) {
        // "강남구" → Sigungu.GANGNAM
        Sigungu sigungu = Sigungu.fromName(sigunguName);
        return policyService.findPoliciesBySigungu(sigungu);
    }
}
