package com.example.youthy.config.dev;

import com.example.youthy.config.CurrentMember;
import com.example.youthy.domain.Member;
import com.example.youthy.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.MethodParameter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.List;

@Configuration
@Profile("local")                         // ← local 프로필일 때만 동작
@RequiredArgsConstructor
public class LocalMockConfig implements WebMvcConfigurer, HandlerMethodArgumentResolver {

    private final MemberRepository memberRepository;

    @Override public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(0, this);          // 우선 적용
    }

    @Override public boolean supportsParameter(MethodParameter p) {
        return p.hasParameterAnnotation(CurrentMember.class)
                && Member.class.isAssignableFrom(p.getParameterType());
    }

    @Override public Object resolveArgument(MethodParameter p, ModelAndViewContainer m,
                                            NativeWebRequest w,
                                            org.springframework.web.bind.support.WebDataBinderFactory b) {
        Long kakaoId = 999L;             // 고정 테스트 사용자
        return memberRepository.findByKakaoId(kakaoId)
                .orElseGet(() -> memberRepository.save(
                        Member.builder()
                                .kakaoId(kakaoId)
                                .email("local@test.com")
                                .username("로컬테스트")
                                .build()
                ));
    }
}
