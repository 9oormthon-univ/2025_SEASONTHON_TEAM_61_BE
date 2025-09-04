// src/main/java/com/example/youthy/config/OpenApiConfig.java
package com.example.youthy.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;

import java.util.Arrays;

@Configuration
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class OpenApiConfig {

    /**
     * 전역 OpenAPI 메타:
     * - 문서 제목/버전
     * - 기본적으로 Bearer 인증 요구(🔒 아이콘 표시)
     *
     * 공개 엔드포인트(예: /kakao/**)는 개별 메서드에
     *   @Operation(security = {})
     * 를 붙이면 전역 요구가 해제됩니다.
     */
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info().title("Youthy API").version("v1"))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }

    /**
     * 그룹 설정 + 커스터마이저:
     * - @CurrentMember 파라미터를 Swagger 파라미터 목록에서 숨김
     *   (springdoc이 커스텀 리졸버를 쿼리 파라미터로 오해하는 문제 해결)
     */
    @Bean
    public GroupedOpenApi api() {
        return GroupedOpenApi.builder()
                .group("api")
                .pathsToMatch("/api/**", "/kakao/**")
                .addOperationCustomizer(hideCurrentMemberParam())
                .build();
    }

    @Bean
    public OperationCustomizer hideCurrentMemberParam() {
        return (operation, handlerMethod) -> {
            if (hasCurrentMemberParam(handlerMethod) && operation.getParameters() != null) {
                // 컨트롤러 파라미터명이 보통 'member' 또는 'authMember'일 확률이 높음
                operation.getParameters().removeIf(p ->
                        "member".equals(p.getName()) || "authMember".equals(p.getName()));
            }
            return operation;
        };
    }

    private boolean hasCurrentMemberParam(HandlerMethod method) {
        return Arrays.stream(method.getMethodParameters())
                .anyMatch(p -> p.hasParameterAnnotation(CurrentMember.class));
    }
}
