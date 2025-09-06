package com.example.youthy.config;

import com.example.youthy.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // ===== CORS 설정(yml) =====
    @Value("${app.cors.allowed-origins:*}")
    private String allowedOriginsCsv;
    @Value("${app.cors.allowed-methods:GET,POST,PUT,DELETE,OPTIONS,PATCH}")
    private String allowedMethodsCsv;
    @Value("${app.cors.allowed-headers:*}")
    private String allowedHeadersCsv;
    @Value("${app.cors.exposed-headers:Authorization}")
    private String exposedHeadersCsv;
    @Value("${app.cors.allow-credentials:true}")
    private boolean allowCredentials;
    @Value("${app.cors.max-age-seconds:3600}")
    private long maxAgeSeconds;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(split(allowedOriginsCsv))
                .allowedMethods(split(allowedMethodsCsv))
                .allowedHeaders(split(allowedHeadersCsv))
                .exposedHeaders(split(exposedHeadersCsv))
                .allowCredentials(allowCredentials)
                .maxAge(maxAgeSeconds);
    }

    private static String[] split(String csv) {
        if (csv == null || csv.isBlank()) return new String[0];
        String[] arr = csv.split(",");
        for (int i = 0; i < arr.length; i++) arr[i] = arr[i].trim();
        return arr;
    }

    // ===== @CurrentMember 리졸버 등록 =====
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new CurrentMemberArgumentResolver());
    }

    // ===== JwtAuthFilter 등록(SecurityConfig 없이 동작) =====
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Bean
    public FilterRegistrationBean<JwtAuthFilter> jwtAuthFilterRegistration(MemberRepository memberRepository) {
        FilterRegistrationBean<JwtAuthFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new JwtAuthFilter(jwtSecret, memberRepository));
        bean.addUrlPatterns("/api/*");   // /api/** 만 보호 (Swagger, Kakao 등은 무관)
        bean.setOrder(1);
        bean.setName("jwtAuthFilter");
        return bean;
    }
}
