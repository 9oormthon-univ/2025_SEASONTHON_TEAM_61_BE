package com.example.youthy.wonyeong2;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis 관련 설정을 위한 클래스입니다.
 */
@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    /**
     * Redis 서버와의 연결을 관리하는 RedisConnectionFactory를 Bean으로 등록합니다.
     * application.yml에 정의된 host와 port 정보를 사용하여 연결합니다.
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(host, port);
    }

    /**
     * RedisTemplate을 Bean으로 등록하여, 서비스 계층에서 Redis 데이터에 쉽게 접근할 수 있도록 합니다.
     * Key와 Value를 모두 String 형태로 저장하도록 직렬화(Serializer) 설정을 추가합니다.
     */
    @Bean
    public RedisTemplate<String, String> redisTemplate() {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());

        // Key와 Value의 Serializer를 String으로 설정하여, Redis에 저장될 때 문자열로 변환되도록 합니다.
        // 이 설정을 하지 않으면, 알아볼 수 없는 형태로 데이터가 저장될 수 있습니다.
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());

        // Hash Key와 Hash Value의 Serializer도 String으로 설정합니다.
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new StringRedisSerializer());

        return redisTemplate;
    }
}
