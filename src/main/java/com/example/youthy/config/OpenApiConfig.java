// src/main/java/com/example/youthy/config/OpenApiConfig.java
package com.example.youthy.config;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI appOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("Youthy API")
                .version("v1")
                .description("Youthy backend OpenAPI"));
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("public")
                .packagesToScan("com.example.youthy.controller") // 컨트롤러 패키지
                .pathsToMatch("/kakao/**", "/api/**", "/health")   // <= /health 포함!
                .build();
    }
}
