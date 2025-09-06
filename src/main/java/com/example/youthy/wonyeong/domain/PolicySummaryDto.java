package com.example.youthy.wonyeong.domain;

import com.example.youthy.YouthPolicy;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Schema(description = "정책 요약 응답")
public record PolicySummaryDto(
        @Schema(description = "정책번호", example = "P-2025-00123") String policyNo,
        @Schema(description = "정책명", example = "서울시 청년 취업지원금") String policyName,
        @Schema(description = "정책 카테고리", example = "일자리") String policyField,
        @Schema(description = "담당기관", example = "서울특별시 일자리정책과") String agency,
        @Schema(description = "모집 시작일(문자열)", example = "2025-09-01") String applyStartDate,
        @Schema(description = "모집 마감일(문자열)", example = "2025-10-15") String applyEndDate,
        @Schema(description = "모집 현황", example = "OPEN") String recruitStatus
) {
    public static PolicySummaryDto from(YouthPolicy p) {
        // ⬇️ 아래 3개는 실제 필드명/타입에 맞게 수정하세요 (현재 문자열 컬럼 가정)
        String agency = safe(p.getOperatingAgency());   // 담당기관(실제 컬럼명 확인 필요)
        String start  = safe(p.getApplyStartDate());    // 모집 시작일
        String end    = safe(p.getApplyEndDate());      // 모집 마감일
        String status = computeStatus(start, end);
        return new PolicySummaryDto(
                p.getPolicyNo(), p.getPolicyName(), p.getPolicyField(),
                agency, start, end, status
        );
    }

    private static String safe(String s) { return s == null ? "" : s; }

    private static String computeStatus(String start, String end) {
        if (end == null || end.isBlank() || end.contains("상시")) return "ALWAYS";
        try {
            var now = LocalDate.now();
            var s = parseDate(start).orElse(LocalDate.MIN);
            var e = parseDate(end).orElse(LocalDate.MAX);
            if ((now.isEqual(s) || now.isAfter(s)) && (now.isEqual(e) || now.isBefore(e))) return "OPEN";
            if (now.isAfter(e)) return "CLOSED";
            return "OPEN";
        } catch (Exception ignored) { return "OPEN"; }
    }

    private static Optional<LocalDate> parseDate(String s) {
        if (s == null || s.isBlank()) return Optional.empty();
        var fmts = new DateTimeFormatter[]{
                DateTimeFormatter.ofPattern("yyyy-MM-dd"),
                DateTimeFormatter.ofPattern("yyyy.MM.dd"),
                DateTimeFormatter.ofPattern("yyyy/MM/dd")
        };
        for (var f : fmts) {
            try { return Optional.of(LocalDate.parse(s.trim(), f)); }
            catch (Exception ignore) {}
        }
        return Optional.empty();
    }
}
