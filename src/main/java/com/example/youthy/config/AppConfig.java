package com.example.youthy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * 애플리케이션 전반에 사용될 Bean들을 등록하는 설정 클래스
 */
@Configuration
public class AppConfig {

    /**
     * RestTemplate을 Spring 컨테이너에 Bean으로 등록
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}