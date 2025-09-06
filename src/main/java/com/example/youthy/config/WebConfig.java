package com.example.youthy.config;

import com.example.youthy.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${jwt.secret}")
    private String jwtSecret;

    // ✅ JwtAuthFilter를 서블릿 필터 체인에 등록
    @Bean
    public FilterRegistrationBean<JwtAuthFilter> jwtAuthFilterRegistration(MemberRepository memberRepository) {
        FilterRegistrationBean<JwtAuthFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new JwtAuthFilter(jwtSecret, memberRepository));
        bean.addUrlPatterns("/api/*");   // ← 꼭 /api/* 로
        bean.setOrder(1);
        bean.setName("jwtAuthFilter");
        return bean;
    }

    // ✅ @CurrentMember 리졸버 등록
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new CurrentMemberArgumentResolver());
    }
}
