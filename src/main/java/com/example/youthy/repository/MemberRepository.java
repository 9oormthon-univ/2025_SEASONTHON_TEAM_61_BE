package com.example.youthy.repository;

import com.example.youthy.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Member save(Member member); //회원정보를 저장하는 메서드_JPA에서 제공

}
