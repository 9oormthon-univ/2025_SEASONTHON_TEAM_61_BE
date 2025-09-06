package com.example.youthy.wonyeong.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "정책 검색 필터")
public record PolicyFilter(
        @Schema(description = "카테고리 목록(다중 선택)", example = "[\"일자리\",\"교육\"]")
        List<String> categories,

        @Schema(description = "시/도 (Enum 이름)", example = "SEOUL")
        Sido sido,

        @Schema(description = "시군구 (Enum 이름, 시/도와 일치)", example = "NOWON")
        Sigungu sigungu,

        @Schema(description = "대상 키워드(학력/전공/취업상태/특화분야 중 OR 매칭)", example = "대학생")
        String target,

        @Schema(description = "담당기관 키워드", example = "서울시")
        String agency,

        @Schema(description = "모집현황", example = "OPEN")
        RecruitStatus status
) {
        public enum RecruitStatus { OPEN, CLOSED, ALWAYS }
}
