package com.example.youthy.chungheon2;

import com.example.youthy.YouthPolicy;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 정책 상세 조회 시 반환될 모든 정보를 담는 DTO
 */
@Getter
public class PolicyDetailDto {

    private final String policyNo;
    private final String policyName;
    private final String policySummary;
    private final String policyField;
    private final String supportContent;
    private final String operationPeriod;
    private final String applicationPeriod;
    private final String supportScale;
    private final Integer minAge;
    private final Integer maxAge;
    private final String incomeCondition;
    private final String educationRequirement;
    private final String majorRequirement;
    private final String employmentStatus;
    private final String specializedField;
    private final String additionalInfo;
    private final String participationRestriction;
    private final String applicationProcess;
    private final String evaluationAndAnnouncement;
    private final String applicationSite;
    private final String requiredDocuments;
    private final int viewCount;
    private final List<String> residences;

    // YouthPolicy 엔티티를 PolicyDetailDto로 변환하는 생성자
    public PolicyDetailDto(YouthPolicy entity) {
        this.policyNo = entity.getPolicyNo();
        this.policyName = entity.getPolicyName();
        this.policySummary = entity.getPolicySummary();
        this.policyField = entity.getPolicyField();
        this.supportContent = entity.getSupportContent();
        this.operationPeriod = entity.getOperationPeriod();
        this.applicationPeriod = entity.getApplicationPeriod();
        this.supportScale = entity.getSupportScale();
        this.minAge = entity.getMinAge();
        this.maxAge = entity.getMaxAge();
        this.incomeCondition = entity.getIncomeCondition();
        this.educationRequirement = entity.getEducationRequirement();
        this.majorRequirement = entity.getMajorRequirement();
        this.employmentStatus = entity.getEmploymentStatus();
        this.specializedField = entity.getSpecializedField();
        this.additionalInfo = entity.getAdditionalInfo();
        this.participationRestriction = entity.getParticipationRestriction();
        this.applicationProcess = entity.getApplicationProcess();
        this.evaluationAndAnnouncement = entity.getEvaluationAndAnnouncement();
        this.applicationSite = entity.getApplicationSite();
        this.requiredDocuments = entity.getRequiredDocuments();
        this.viewCount = entity.getViewCount();
        this.residences = entity.getResidences().stream()
                .map(residence -> residence.getRegion().getName())
                .collect(Collectors.toList());
    }
}
