// src/main/java/com/example/youthy/config/OpenApiConfig.java
package com.example.youthy.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI appOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Youthy API")
                        .version("v1")
                        .description("Youthy backend OpenAPI"));
    }

    /**
     * 전체 API를 한 그룹에서 모두 노출
     * - 패키지 제한 제거: com.example.youthy 전체 스캔
     * - path 제한 제거: /** 로 모두 매칭
     */
    @Bean
    public GroupedOpenApi allApis() {
        return GroupedOpenApi.builder()
                .group("all")
                .packagesToScan("com.example.youthy")   // controller 뿐 아니라 chungheon 등 하위 모두 스캔
                .pathsToMatch("/**")                    // /kakao/**, /api/**, /admin/**, /health 등 전체 포함
                .build();
    }

    /**
     * (선택) 관리용 API만 분리해서 보고 싶다면 추가 그룹
     */
    @Bean
    public GroupedOpenApi adminApis() {
        return GroupedOpenApi.builder()
                .group("admin")
                .packagesToScan("com.example.youthy.chungheon")
                .pathsToMatch("/admin/**")
                .build();
    }
}
