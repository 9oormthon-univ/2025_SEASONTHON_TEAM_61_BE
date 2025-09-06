package com.example.youthy.wonyeong3.scrap.repository;

import com.example.youthy.YouthPolicy;
import com.example.youthy.domain.Member;
import com.example.youthy.wonyeong3.scrap.domain.Scrap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScrapRepository extends JpaRepository<Scrap, Long> {
    boolean existsByMemberAndPolicy(Member member, YouthPolicy policy);
    void deleteByMemberAndPolicy(Member member, YouthPolicy policy);
    Page<Scrap> findByMemberOrderByCreatedAtDesc(Member member, Pageable pageable);
    long countByMember(Member member);
}
