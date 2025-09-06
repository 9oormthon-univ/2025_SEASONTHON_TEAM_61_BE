// src/main/java/com/example/youthy/config/WebConfig.java
package com.example.youthy.config;

import com.example.youthy.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${jwt.secret}")
    private String jwtSecret;

    // 쉼표(,)로 여러 오리진 허용
    @Value("${app.cors.allowed-origins:http://localhost:3000}")
    private String allowedOrigins;

    private final MemberRepository memberRepository;

    public WebConfig(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    // @CurrentMember 주입
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new CurrentMemberArgumentResolver());
    }

    // CORS 설정 (프로퍼티에서 읽음)
    @Override
    public void addCorsMappings(CorsRegistry r) {
        String[] origins = Arrays.stream(allowedOrigins.split(","))
                .map(String::trim).filter(s -> !s.isEmpty()).toArray(String[]::new);
        r.addMapping("/**")
                .allowedOrigins(origins)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    // JWT 필터 등록: /api/* 만 보호 (Swagger/H2/Kakao 콜백 제외)
    @Bean
    public FilterRegistrationBean<JwtAuthFilter> jwtFilter() {
        FilterRegistrationBean<JwtAuthFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new JwtAuthFilter(jwtSecret, memberRepository));
        bean.addUrlPatterns("/api/*");
        bean.setOrder(1);
        return bean;
    }
}
