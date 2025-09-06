package com.example.youthy.config;

import com.example.youthy.domain.Member;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class CurrentMemberArgumentResolver implements HandlerMethodArgumentResolver {

    public static final String REQ_ATTR = "authMember"; // 필터와 동일!

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentMember.class)
                && Member.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter, ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {

        HttpServletRequest req = webRequest.getNativeRequest(HttpServletRequest.class);
        Object attr = (req != null) ? req.getAttribute(REQ_ATTR) : null;

        // 🔴 인증 실패/미주입이면 반드시 null 반환 (절대 new Member() 하지 말 것)
        return (attr instanceof Member) ? (Member) attr : null;
    }
}
