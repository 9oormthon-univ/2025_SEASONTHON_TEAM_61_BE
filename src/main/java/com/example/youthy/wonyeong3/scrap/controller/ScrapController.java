package com.example.youthy.wonyeong3.scrap.controller;

import com.example.youthy.config.CurrentMember;
import com.example.youthy.domain.Member;
import com.example.youthy.wonyeong3.scrap.dto.ScrapItemDto;
import com.example.youthy.wonyeong3.scrap.service.ScrapService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Scrap", description = "회원별 정책 스크랩 API")
@RestController
@RequestMapping("/api/wonyeong3/scraps")
@RequiredArgsConstructor
public class ScrapController {

    private final ScrapService scrapService;

    @Operation(summary = "정책 스크랩 추가")
    @PostMapping("/{policyNo}")
    public ResponseEntity<Void> add(
            @CurrentMember Member member,
            @PathVariable String policyNo
    ) {
        scrapService.add(member, policyNo);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "정책 스크랩 해제")
    @DeleteMapping("/{policyNo}")
    public ResponseEntity<Void> remove(
            @CurrentMember Member member,
            @PathVariable String policyNo
    ) {
        scrapService.remove(member, policyNo);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "내 스크랩 목록 조회 (최신순)")
    @GetMapping
    public ResponseEntity<Page<ScrapItemDto>> list(
            @CurrentMember Member member,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(scrapService.list(member, page, size));
    }

    @Operation(summary = "내 스크랩 총 개수")
    @GetMapping("/count")
    public ResponseEntity<Map<String, Object>> count(
            @CurrentMember Member member
    ) {
        long count = scrapService.count(member);
        return ResponseEntity.ok(Map.of("count", count));
    }
}
