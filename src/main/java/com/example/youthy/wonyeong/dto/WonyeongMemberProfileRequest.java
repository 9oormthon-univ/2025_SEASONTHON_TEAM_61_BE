package com.example.youthy.wonyeong.dto;

import com.example.youthy.wonyeong.domain.AgeGroup;
import com.example.youthy.wonyeong.domain.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(description = "wonyeong 회원 프로필 저장 요청 DTO")
public record WonyeongMemberProfileRequest(

        @Schema(description = "기존 Member ID (memberId 또는 kakaoId 중 하나 필수)", example = "1")
        Long memberId,

        @Schema(description = "카카오 ID (memberId 대신 사용할 수 있음)", example = "123456789")
        Long kakaoId,

        @NotNull
        @Schema(description = "연령대 (한글 값)", example = "20대")
        AgeGroup ageGroup,

        @NotNull
        @Schema(description = "관심 카테고리 목록(한글 값)", example = "[\"취업\",\"교육\",\"복지\"]")
        List<Category> categories
) {}
