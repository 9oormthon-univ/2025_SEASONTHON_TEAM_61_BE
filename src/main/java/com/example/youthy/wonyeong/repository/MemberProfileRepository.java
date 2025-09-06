package com.example.youthy.wonyeong.repository;

import com.example.youthy.wonyeong.domain.MemberProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberProfileRepository
        extends JpaRepository<MemberProfile, Long> {

    Optional<MemberProfile> findByMember_Id(Long memberId);
}
