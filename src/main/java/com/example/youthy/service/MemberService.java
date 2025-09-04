// src/main/java/com/example/youthy/service/MemberService.java
package com.example.youthy.service;

import com.example.youthy.domain.Member;
import com.example.youthy.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public void bumpTokenVersion(Long memberId) {
        Member m = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("member not found"));
        m.bumpTokenVersion(); // JPA dirty checking
    }
}
