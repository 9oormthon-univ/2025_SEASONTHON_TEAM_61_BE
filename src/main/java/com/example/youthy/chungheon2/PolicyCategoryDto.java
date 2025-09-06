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
        this.dDay = calculateDday(entity.getApplicationEndDate());
        this.policySummary = entity.getPolicySummary();
    }

    /**
     * 신청 기간 문자열을 바탕으로 D-Day를 계산하는 헬퍼 메서드
     * @return "D-7", "D-Day", "마감", "상시" 등의 D-Day 정보
     */
    private String calculateDday(LocalDate endDate) {
        // DB에 기본값으로 저장된 아주 먼 미래의 날짜를 정의합니다.
        final LocalDate FAR_FUTURE_DATE = LocalDate.of(9999, 12, 31);

        // 1. 종료일이 없거나(null), 아주 먼 미래의 날짜(기본값)이면 "상시"로 표시합니다.
        if (endDate == null || endDate.equals(FAR_FUTURE_DATE)) {
            return "상시";
        }

        // 2. 유효한 종료일을 기준으로 D-Day를 정확하게 계산합니다.
        LocalDate today = LocalDate.now();
        long daysLeft = ChronoUnit.DAYS.between(today, endDate);

        if (daysLeft < 0) {
            return "마감";
        } else if (daysLeft == 0) {
            return "D-Day";
        } else {
            return "D-" + daysLeft;
        }
    }
}
