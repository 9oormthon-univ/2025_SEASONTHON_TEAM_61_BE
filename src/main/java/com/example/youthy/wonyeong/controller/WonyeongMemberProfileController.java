package com.example.youthy.wonyeong.controller;

import com.example.youthy.wonyeong.dto.WonyeongMemberProfileRequest;
import com.example.youthy.wonyeong.dto.WonyeongMemberProfileResponse;
import com.example.youthy.wonyeong.service.WonyeongMemberProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Wonyeong - Member Profile")
@RestController
@RequestMapping("/api/wonyeong/members/profile")
@RequiredArgsConstructor
public class WonyeongMemberProfileController {

    private final WonyeongMemberProfileService service;

    @Operation(summary = "회원 프로필 저장(연령대 + 관심 카테고리)", description = "memberId 또는 kakaoId 로 대상을 지정하고 저장합니다.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WonyeongMemberProfileResponse upsert(@Valid @RequestBody WonyeongMemberProfileRequest request) {
        return service.upsertProfile(request);
    }
}
