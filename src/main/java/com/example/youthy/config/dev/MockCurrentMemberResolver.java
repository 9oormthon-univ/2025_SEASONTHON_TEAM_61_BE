package com.example.youthy.config.dev;

import com.example.youthy.config.CurrentMember;
import com.example.youthy.domain.Member;
import com.example.youthy.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.context.request.NativeWebRequest;

@Component
@Profile("local")   // 로컬에서만 활성화
@RequiredArgsConstructor
public class MockCurrentMemberResolver implements HandlerMethodArgumentResolver {

    private final MemberRepository memberRepository;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentMember.class)
                && Member.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  org.springframework.web.bind.support.WebDataBinderFactory binderFactory) {

        Long kakaoId = 999L; // 고정 모킹 ID
        return memberRepository.findByKakaoId(kakaoId)
                .orElseGet(() -> memberRepository.save(
                        Member.builder()
                                .kakaoId(kakaoId)
                                .email("local@test.com")
                                .username("로컬테스트")   // name() 말고 username()
                                .build()
                ));
    }
}
