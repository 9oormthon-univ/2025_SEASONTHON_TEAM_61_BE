package com.example.youthy.wonyeong.controller;

import com.example.youthy.wonyeong.dto.MemberProfileRequest;
import com.example.youthy.wonyeong.dto.MemberProfileResponse;
import com.example.youthy.wonyeong.service.MemberProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Member Profile")
@RestController
@RequestMapping("/api/v1/members/profile")
@RequiredArgsConstructor
public class MemberProfileController {

    private final MemberProfileService service;

    @Operation(summary = "회원 프로필 저장(연령대 + 관심 카테고리)", description = "memberId 또는 kakaoId 로 대상을 지정하고 저장합니다.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MemberProfileResponse upsert(@Valid @RequestBody MemberProfileRequest request) {
        return service.upsertProfile(request);
    }
}
