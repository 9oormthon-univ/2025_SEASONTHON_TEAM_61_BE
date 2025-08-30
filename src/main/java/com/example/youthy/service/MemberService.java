package com.example.youthy.service;

import com.example.youthy.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor // 의존성 주입
@Service
public class MemberService {

    private final MemberRepository memberRepository;

}
