// src/main/java/com/example/youthy/config/SecurityConfig.java
package com.example.youthy.config;

import com.example.youthy.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    private final String jwtSecret;
    private final MemberRepository memberRepository;
    private final String frontUrl;

    public SecurityConfig(
            @Value("${jwt.secret}") String jwtSecret,
            MemberRepository memberRepository,
            @Value("${app.front-url:http://localhost:3000}") String frontUrl
    ) {
        this.jwtSecret = jwtSecret;
        this.memberRepository = memberRepository;
        this.frontUrl = frontUrl;
    }

    @Bean
    public SecurityFilterChain appChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())

                // ⚠️ DEV 단계: 카카오 흐름/콘솔은 CSRF 예외. 운영에서는 CSRF 토큰 전략 고려 권장.
                .csrf(csrf -> csrf.ignoringRequestMatchers(
                        "/h2-console/**", "/swagger-ui/**", "/v3/api-docs/**",
                        "/kakao/**" // login/callback/refresh/logout 무상태 호출
                ))

                .headers(h -> h.frameOptions(f -> f.sameOrigin())) // H2 콘솔 허용

                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/h2-console/**",
                                "/swagger-ui/**", "/v3/api-docs/**",
                                "/error", "/favicon.ico"
                        ).permitAll()

                        // 로그인 전 단계는 허용
                        .requestMatchers("/kakao/auth/login", "/kakao/callback", "/kakao/auth/refresh").permitAll()

                        // 로그아웃은 인증 필요
                        .requestMatchers("/kakao/auth/logout").authenticated()

                        // 일반 API는 인증
                        .requestMatchers("/api/**").authenticated()

                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .anyRequest().permitAll()
                )

                .addFilterBefore(new JwtAuthFilter(jwtSecret, memberRepository),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowedOrigins(List.of(frontUrl)); // http://localhost:3000
        cfg.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
        cfg.setAllowedHeaders(List.of("Content-Type", "X-Requested-With"));
        cfg.setAllowCredentials(true); // 쿠키 전송
        UrlBasedCorsConfigurationSource src = new UrlBasedCorsConfigurationSource();
        src.registerCorsConfiguration("/**", cfg);
        return src;
    }
}
