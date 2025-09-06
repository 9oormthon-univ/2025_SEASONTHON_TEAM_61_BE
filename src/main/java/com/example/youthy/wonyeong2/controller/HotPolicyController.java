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

@Tag(name = "Hot", description = "viewCount 기반 인기 정책(시간 캐시) API")
@RestController
@RequestMapping("/api/v1/policies")
@RequiredArgsConstructor
public class HotPolicyController {

    private final HotPolicyService service;

    @Operation(summary = "인기 정책 TopN (캐시된 결과)", description = "1시간마다 동기화된 viewCount TopN을 반환합니다. 기본 10개.")
    @GetMapping("/hot")
    public ResponseEntity<Map<String, Object>> getHot(
            @RequestParam(name = "limit", defaultValue = "10") int limit
    ) {
        List<HotPolicyDto> data = service.getHot(limit);
        Map<String, Object> body = new HashMap<>();
        body.put("last_refreshed_at", service.getLastRefreshedAt()); // ISO-8601
        body.put("count", data.size());
        body.put("items", data);
        return ResponseEntity.ok(body);
    }

    @Operation(summary = "(관리) 캐시 강제 갱신", description = "즉시 DB에서 재계산하여 캐시를 갱신합니다.")
    @PostMapping("/hot/refresh")
    public ResponseEntity<Map<String, Object>> refresh(
            @RequestParam(name = "limit", defaultValue = "10") int limit
    ) {
        service.refreshCache(limit);
        Map<String, Object> body = new HashMap<>();
        body.put("last_refreshed_at", service.getLastRefreshedAt());
        body.put("status", "ok");
        return ResponseEntity.ok(body);
    }
}
