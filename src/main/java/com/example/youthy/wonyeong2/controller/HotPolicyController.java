package com.example.youthy.wonyeong2.controller;

import com.example.youthy.wonyeong2.dto.HotPolicyDto;
import com.example.youthy.wonyeong2.service.HotPolicyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "Hot Policies", description = "실시간 인기 정책 API")
@RestController
@RequestMapping("/api/v1/policies")
@RequiredArgsConstructor
public class HotPolicyController {

    private final HotPolicyService hotPolicyService;

    @Operation(summary = "인기 정책 TopN (실시간)", description = "Redis Sorted Set을 기반으로 한 실시간 인기 정책 TopN을 반환합니다. 기본 10개.")
    @GetMapping("/hot")
    public ResponseEntity<Map<String, Object>> getHot(
            @RequestParam(name = "limit", defaultValue = "10") int limit
    ) {
        List<HotPolicyDto> data = hotPolicyService.getHotPolicies(limit);
        Map<String, Object> body = new HashMap<>();
        body.put("count", data.size());
        body.put("items", data);
        return ResponseEntity.ok(body);
    }
}
