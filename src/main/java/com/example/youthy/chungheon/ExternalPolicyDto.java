package com.example.youthy.chungheon;

import com.example.youthy.YouthPolicy;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.StringJoiner;

/**
 * 외부 청년정책 Open API의 응답을 매핑하기 위한 DTO 클래스입니다.
 * 개발자 A의 핵심 책임 영역입니다.
 */
public class ExternalPolicyDto {

    /**
     * 최상위 응답을 감싸는 Wrapper 클래스
     * API 응답 형식: { "resultCode": ..., "result":{...} }
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class YouthPolicyApiResponse {
        private ResultData result; // 'result' 객체를 받기 위한 필드
    }

    /**
     * 'result' 객체 내부를 감싸는 Wrapper 클래스
     * API 응답 형식: { "pagging": ... , "youthPolicyList": [ ... ] }
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ResultData {
        private List<YouthPolicyItem> youthPolicyList;
    }

    /**
     * 개별 정책 아이템의 모든 필드를 담는 클래스
     * API 응답 파라미터명과 일치하는 필드명을 사용합니다.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true) // API 응답에 모르는 필드가 있어도 무시하고 파싱합니다.
    public static class YouthPolicyItem {

        // --- API 응답 필드 ---
        private String plcyNo;              // 정책번호
        private String plcyNm;              // 정책명
        private String lclsfNm;             // 정책대분류명
        private String mclsfNm;             // 정책중분류명
        private String plcySprtCn;          // 정책지원내용
        private String bizPrdBgngYmd;       // 사업기간시작일자
        private String bizPrdEndYmd;        // 사업기간종료일자
        private String aplyYmd;             // 신청기간
        private String sprtSclCnt;          // 지원규모수
        private Integer sprtTrgtMinAge;     // 지원대상최소연령
        private Integer sprtTrgtMaxAge;     // 지원대상최대연령
        private String zipCd;               // 정책거주지역코드
        private String earnMinAmt;          // 소득최소금액
        private String earnMaxAmt;          // 소득최대금액
        private String earnEtcCn;           // 소득기타내용
        private String schoolCd;            // 정책학력요건코드
        private String plcyMajorCd;         // 정책전공요건코드
        private String jobCd;               // 정책취업요건코드
        private String sBizCd;              // 정책특화요건코드
        private String etcMttrCn;           // 기타사항내용
        private String ptcpPrpTrgtCn;       // 참여제안대상내용
        private String plcyAplyMthdCn;      // 정책신청방법내용
        private String srngMthdCn;          // 심사방법내용
        private String aplyUrlAddr;         // 신청URL주소
        private String sbmsnDcmntCn;        // 제출서류내용

        /**
         * 외부 API DTO 객체를 우리 서비스의 DB Entity 객체로 변환하는 핵심 메서드입니다.
         * 여기서 데이터를 우리 서비스에 맞게 '가공'하고 '조합'합니다.
         * @return YouthPolicy 엔티티
         */
        public YouthPolicy toEntity() {
            // 1. 먼저 residence를 제외한 기본 YouthPolicy 객체를 생성합니다.
            YouthPolicy policy = YouthPolicy.builder()
                    .policyNo(this.plcyNo)
                    .policyName(this.plcyNm)
                    .policyField(combineFields(this.lclsfNm, this.mclsfNm, " / "))
                    .supportContent(this.plcySprtCn)
                    .operationPeriod(combineFields(this.bizPrdBgngYmd, this.bizPrdEndYmd, " ~ "))
                    .applicationPeriod(this.aplyYmd)
                    .supportScale(this.sprtSclCnt)
                    .minAge(this.sprtTrgtMinAge)
                    .maxAge(this.sprtTrgtMaxAge)
                    .incomeCondition(buildIncomeCondition())
                    .educationRequirement(this.schoolCd)
                    .majorRequirement(this.plcyMajorCd)
                    .employmentStatus(this.jobCd)
                    .specializedField(this.sBizCd)
                    .additionalInfo(this.etcMttrCn)
                    .participationRestriction(this.ptcpPrpTrgtCn)
                    .applicationProcess(this.plcyAplyMthdCn)
                    .evaluationAndAnnouncement(this.srngMthdCn)
                    .applicationSite(this.aplyUrlAddr)
                    .requiredDocuments(this.sbmsnDcmntCn)
                    .build();

            // 2. 생성된 policy 객체에 residence 정보(들)를 추가합니다.
            if (StringUtils.hasText(this.zipCd)) {
                String[] zipCodes = this.zipCd.split(",");
                for (String code : zipCodes) {
                    if (StringUtils.hasText(code)) {
                        policy.addResidence(code.trim());
                    }
                }
            }

            // 3. 모든 정보가 채워진 policy 객체를 반환합니다.
            return policy;
        }

        // Helper 메서드: 여러 필드를 하나의 문자열로 조합
        private String combineFields(String field1, String field2, String delimiter) {
            StringJoiner joiner = new StringJoiner(delimiter);
            if (StringUtils.hasText(field1)) joiner.add(field1);
            if (StringUtils.hasText(field2)) joiner.add(field2);
            return joiner.toString();
        }

        // Helper 메서드: 소득 조건 정보를 하나의 문자열로 조합
        private String buildIncomeCondition() {
            StringJoiner joiner = new StringJoiner("\n");
            boolean isIncomeUnlimited ="0".equals(this.earnMinAmt) && "0".equals(this.earnMaxAmt);
            if(isIncomeUnlimited){
                joiner.add("소득범위: 제한없음");
            }
            else if(StringUtils.hasText(this.earnMinAmt) || StringUtils.hasText(this.earnMaxAmt)) {
                String range = String.format("소득범위: %s원 ~ %s원", this.earnMinAmt, this.earnMaxAmt);
                joiner.add(range);
            }
            if(StringUtils.hasText(this.earnEtcCn)){
                joiner.add("기타: " + this.earnEtcCn);
            }
            return joiner.toString();
        }
    }
}

