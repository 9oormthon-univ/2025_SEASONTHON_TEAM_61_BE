// src/main/java/com/example/youthy/config/OpenApiConfig.java
package com.example.youthy.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class OpenApiConfig {

    /** 프론트 전용 문서: 여기에 명시한 경로만 Swagger에 노출 */
    @Bean
    public GroupedOpenApi frontendApi() {
        return GroupedOpenApi.builder()
                .group("frontend")
                .pathsToMatch(
                        "/kakao/callback",
                        "/kakao/auth/refresh",
                        "/kakao/auth/logout",
                        "/api/me"
                )
                .build();
    }

    /** (선택) 운영/내부 문서 */
    // @Bean
    // public GroupedOpenApi adminApi() {
    //     return GroupedOpenApi.builder()
    //         .group("admin")
    //         .pathsToMatch("/kakao/auth/logout-all", "/kakao/logout-url")
    //         .build();
    // }
}
