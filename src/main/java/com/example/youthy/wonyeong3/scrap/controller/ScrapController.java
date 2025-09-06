package com.example.youthy.wonyeong3.scrap.controller;

import com.example.youthy.config.CurrentMember; // 프로젝트에 이미 있는 경우
import com.example.youthy.domain.Member;
import com.example.youthy.wonyeong3.scrap.domain.Scrap;
import com.example.youthy.wonyeong3.scrap.service.ScrapService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/wonyeong3/scraps")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearer-key") // Swagger에서 Authorize 버튼 활성
public class ScrapController {

    private final ScrapService scrapService;

    @Operation(summary = "정책 스크랩 추가 (회원별)")
    @PostMapping("/{policyNo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void add(@CurrentMember Member member, @PathVariable String policyNo) {
        try {
            scrapService.add(member, policyNo);
        } catch (IllegalStateException e) {
            if ("ALREADY_SCRAPPED".equals(e.getMessage())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 스크랩한 정책입니다.");
            }
            throw e;
        }
    }

    @Operation(summary = "스크랩 목록 조회 (회원별)")
    @GetMapping
    public Page<Scrap> list(
            @CurrentMember Member member,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Positive int size
    ) {
        return scrapService.list(member, page, size);
    }

    @Operation(summary = "스크랩 개수 조회 (회원별)")
    @GetMapping("/count")
    public long count(@CurrentMember Member member) {
        return scrapService.count(member);
    }

    @Operation(summary = "스크랩 삭제 (회원별)")
    @DeleteMapping("/{policyNo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@CurrentMember Member member, @PathVariable String policyNo) {
        scrapService.remove(member, policyNo);
    }
}
