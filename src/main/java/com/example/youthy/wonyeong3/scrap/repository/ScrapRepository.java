package com.example.youthy.wonyeong3.scrap.repository;

import com.example.youthy.domain.Member;
import com.example.youthy.wonyeong3.scrap.domain.Scrap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScrapRepository extends JpaRepository<Scrap, Long> {

    boolean existsByMemberAndPolicyNo(Member member, String policyNo);

    Page<Scrap> findByMember(Member member, Pageable pageable);

    long countByMember(Member member);

    void deleteByMemberAndPolicyNo(Member member, String policyNo);
}
