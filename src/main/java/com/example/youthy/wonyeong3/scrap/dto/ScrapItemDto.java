package com.example.youthy.wonyeong3.scrap.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ScrapItemDto {
    private String policyNo;
    private String policyName;
    private int viewCount;
    private LocalDateTime createdAt;
}
