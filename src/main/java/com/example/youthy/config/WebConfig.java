package com.example.youthy.config;

import com.example.youthy.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.*;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${jwt.secret}")
    private String jwtSecret;

    // ✅ 필터를 여기서만 등록 (이름 충돌 방지용 Bean name 부여)
    @Bean(name = "jwtAuthFilterRegistration")
    public FilterRegistrationBean<JwtAuthFilter> jwtAuthFilter(MemberRepository memberRepository) {
        var bean = new FilterRegistrationBean<>(new JwtAuthFilter(jwtSecret, memberRepository));
        bean.setName("jwtAuthFilter");  // 서블릿 필터 이름
        bean.addUrlPatterns("/api/*");  // /api/** 만 보호
        bean.setOrder(1);               // 실행 순서
        return bean;
    }

    // ✅ @CurrentMember 리졸버 등록
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new CurrentMemberArgumentResolver());
    }

    // (선택) CORS
    @Override
    public void addCorsMappings(CorsRegistry r) {
        r.addMapping("/**")
                .allowedOrigins("http://localhost:3000")
                .allowedMethods("GET","POST","PUT","DELETE","OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("Authorization")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
