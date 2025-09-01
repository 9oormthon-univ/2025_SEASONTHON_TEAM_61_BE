// src/main/java/com/example/youthy/repository/MemberRepository.java
package com.example.youthy.repository;

import com.example.youthy.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByKakaoId(Long kakaoId);
}
