package com.example.youthy.wonyeong.dto;

import com.example.youthy.wonyeong.domain.AgeGroup;
import com.example.youthy.wonyeong.domain.Category;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Set;

@Schema(description = "wonyeong 회원 프로필 응답 DTO")
public record WonyeongMemberProfileResponse(

        @Schema(description = "프로필 PK", example = "10")
        Long profileId,

        @Schema(description = "회원 ID", example = "1")
        Long memberId,

        @Schema(description = "연령대 (한글 값)", example = "30대")
        AgeGroup ageGroup,

        @Schema(description = "관심 카테고리(한글 값)", example = "[\"취업\",\"교육\",\"기타\"]")
        Set<Category> categories
) {}
