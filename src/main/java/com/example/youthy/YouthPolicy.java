package com.example.youthy;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

/**
 * 청년 정책 정보를 저장하는 데이터베이스 테이블과 매핑되는 JPA 엔티티 클래스
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class YouthPolicy {

    @Id
    @Column(name = "policy_no", length = 50)
    private String policyNo; // 정책번호 (plcyNo)

    @Column(name = "policy_name", nullable = false)
    private String policyName; // 정책명 (plcyNm)

    @Column(name = "policy_summary", columnDefinition = "TEXT")
    private String policySummary; // 정책설명(plcyExplnCn)

    @Column(name = "policy_field")
    private String policyField; // 정책분야 (lclsfNm, mclsfNm)

    @Column(name = "support_content", columnDefinition = "TEXT")
    private String supportContent; // 지원내용 (plcySprtCn)

    @Column(name = "operation_period", length = 500)
    private String operationPeriod; // 사업 운영 기간 (bizPrdBgngYmd, bizPrdEndYmd)

    @Column(name = "application_period", length = 500)
    private String applicationPeriod; // 사업 신청 기간 (aplyYmd)

    @Column(name = "support_scale")
    private String supportScale; // 지원 규모(명) (sprtSclCnt)

    @Column(name = "min_age")
    private Integer minAge; // 지원 연령 (최소) (sprtTrgtMinAge)

    @Column(name = "max_age")
    private Integer maxAge; // 지원 연령 (최대) (sprtTrgtMaxAge)

    @OneToMany(mappedBy = "youthPolicy", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PolicyResidence> residences = new HashSet<>();

    @Column(name = "income_condition",columnDefinition = "TEXT")
    private String incomeCondition; // 소득 조건 (earnMinAmt, earnMaxAmt 등)

    @Column(name = "education_requirement")
    private String educationRequirement; // 학력 요건 (schoolCd)

    @Column(name = "major_requirement")
    private String majorRequirement; // 전공 요건 (plcyMajorCd)

    @Column(name = "employment_status")
    private String employmentStatus; // 취업상태 요건 (jobCd)

    @Column(name = "specialized_field")
    private String specializedField; // 특화분야 (sBizCd)

    @Column(name = "additional_info",columnDefinition = "TEXT")
    private String additionalInfo; // 추가사항 (etcMttrCn)

    @Column(name = "participation_restriction",columnDefinition = "TEXT")
    private String participationRestriction; // 참여제한 대상 (ptcpPrpTrgtCn)

    @Column(name = "application_process",columnDefinition = "TEXT")
    private String applicationProcess; // 신청절차 (plcyAplyMthdCn)

    @Column(name = "evaluation_announcement",columnDefinition = "TEXT")
    private String evaluationAndAnnouncement; // 심사 및 발표 (srngMthdCn)

    @Column(name = "application_site", length = 1000)
    private String applicationSite; // 신청 사이트 (aplyUrlAddr)

    @Column(name = "required_documents",columnDefinition = "TEXT")
    private String requiredDocuments; // 제출 서류 (sbmsnDcmntCn)
  
    // **조회수 필드 추가**
    @Column(name = "view_count")
    private int viewCount = 0; // 조회수 (Youthy 서비스 자체 관리)

    @Builder
    public YouthPolicy(String policyNo, String policyName, String policySummary, String policyField, String supportContent, String operationPeriod, String applicationPeriod, String supportScale, Integer minAge, Integer maxAge, String incomeCondition, String educationRequirement, String majorRequirement, String employmentStatus, String specializedField, String additionalInfo, String participationRestriction, String applicationProcess, String evaluationAndAnnouncement, String applicationSite, String requiredDocuments) {
        this.policyNo = policyNo;
        this.policyName = policyName;
        this.policySummary = policySummary;
        this.policyField = policyField;
        this.supportContent = supportContent;
        this.operationPeriod = operationPeriod;
        this.applicationPeriod = applicationPeriod;
        this.supportScale = supportScale;
        this.minAge = minAge;
        this.maxAge = maxAge;
        this.incomeCondition = incomeCondition;
        this.educationRequirement = educationRequirement;
        this.majorRequirement = majorRequirement;
        this.employmentStatus = employmentStatus;
        this.specializedField = specializedField;
        this.additionalInfo = additionalInfo;
        this.participationRestriction = participationRestriction;
        this.applicationProcess = applicationProcess;
        this.evaluationAndAnnouncement = evaluationAndAnnouncement;
        this.applicationSite = applicationSite;
        this.requiredDocuments = requiredDocuments;
    }
    //== 연관관계 편의 메서드 ==//
    public void addResidence(String zipCode) {
        PolicyResidence residence = new PolicyResidence(this, zipCode);
        this.residences.add(residence);
    }
}
