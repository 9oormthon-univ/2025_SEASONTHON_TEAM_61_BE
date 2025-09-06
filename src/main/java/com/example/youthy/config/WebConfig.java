package com.example.youthy.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // application.yml에 정의된 값을 주입받습니다.
    @Value("${app.cors.allowed-origins}")
    private String allowedOrigins;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 1. 모든 경로에 대해 CORS 정책을 적용합니다.
                .allowedOrigins(allowedOrigins.split(",")) // 2. yml 파일에서 주입받은, 허용할 출처(프론트엔드 주소)를 설정합니다.
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 3. 허용할 HTTP 메서드를 지정합니다.
                .allowedHeaders("*") // 4. 허용할 HTTP 헤더를 지정합니다.
                .allowCredentials(true) // 5. 쿠키 등 자격 증명 정보를 허용합니다.
                .maxAge(3600); // 6. Pre-flight 요청의 캐시 시간을 설정합니다. (초 단위)
    }
}