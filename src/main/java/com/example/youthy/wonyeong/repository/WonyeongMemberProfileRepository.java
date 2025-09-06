package com.example.youthy.wonyeong.repository;

import com.example.youthy.wonyeong.domain.WonyeongMemberProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WonyeongMemberProfileRepository
        extends JpaRepository<WonyeongMemberProfile, Long> {

    Optional<WonyeongMemberProfile> findByMember_Id(Long memberId);
}
