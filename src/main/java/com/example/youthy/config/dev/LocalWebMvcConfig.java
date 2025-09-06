package com.example.youthy.config.dev;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@Profile("local")
@RequiredArgsConstructor
public class LocalWebMvcConfig implements WebMvcConfigurer {

    private final MockCurrentMemberResolver mockCurrentMemberResolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        // 0번에 추가해서 우선 적용되도록
        resolvers.add(0, mockCurrentMemberResolver);
    }
}
