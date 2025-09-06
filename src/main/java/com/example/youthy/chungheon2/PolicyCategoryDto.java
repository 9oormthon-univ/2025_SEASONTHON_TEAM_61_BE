package com.example.youthy.chungheon2;

import com.example.youthy.YouthPolicy;
import lombok.Getter;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * 카테고리별 정책 목록 조회 시 반환될 응답 DTO
 */
@Getter
public class PolicyCategoryDto {

    private final String policyNo;      // 정책번호
    private final String policyName;    // 정책이름
    private final String category;      // 카테고리 (정책분야)
    private final String dDay;          // D-Day 정보
    private final String policySummary;

    public PolicyCategoryDto(YouthPolicy entity) {
        this.policyNo = entity.getPolicyNo();
        this.policyName = entity.getPolicyName();
        this.category = entity.getPolicyField();
        this.dDay = calculateDday(entity.getApplicationPeriod());
        this.policySummary = entity.getPolicySummary();
    }

    /**
     * 신청 기간 문자열을 바탕으로 D-Day를 계산하는 헬퍼 메서드
     * @param applicationPeriod "20250601 ~ 20251130", "상시" 등의 문자열
     * @return "D-7", "D-Day", "마감", "상시" 등의 D-Day 정보
     */
    private String calculateDday(String applicationPeriod) {
        if (!StringUtils.hasText(applicationPeriod) || applicationPeriod.contains("상시")) {
            return "상시";
        }

        String endDateStr = applicationPeriod.trim();

        if (endDateStr.contains("~")) {
            endDateStr = endDateStr.split("~")[1].trim();
        }

        if (!StringUtils.hasText(endDateStr)) {
            return "정보 확인 필요";
        }

        try {
            LocalDate endDate = LocalDate.parse(endDateStr, DateTimeFormatter.ofPattern("yyyyMMdd"));
            LocalDate today = LocalDate.now();
            long daysLeft = ChronoUnit.DAYS.between(today, endDate);

            if (daysLeft < 0) {
                return "마감";
            } else if (daysLeft == 0) {
                return "D-Day";
            } else {
                return "D-" + daysLeft;
            }
        } catch (Exception e) {
            return "정보 확인 필요"; // 날짜 형식 파싱 실패 시
        }
    }
}
