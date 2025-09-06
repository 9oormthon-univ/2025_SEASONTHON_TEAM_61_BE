package com.example.youthy.wonyeong2.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class HotPolicyDto {
    private String policyNo;   // YouthPolicy의 @Id (String)
    private String policyName; // YouthPolicy의 정책명
    private int viewCount;     // 조회수
}
