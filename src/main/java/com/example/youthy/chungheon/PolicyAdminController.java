package com.example.youthy.chungheon;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/api/v1/policies")
public class PolicyAdminController {

    private final PolicyUpdateService policyUpdateService;

    @PostMapping("/update")
    public ResponseEntity<String> forceUpdatePolicies() {
        policyUpdateService.updateAllPoliciesFromApi(); // 수동으로 서비스 실행
        return ResponseEntity.ok("Policy update process started successfully.");
    }
}