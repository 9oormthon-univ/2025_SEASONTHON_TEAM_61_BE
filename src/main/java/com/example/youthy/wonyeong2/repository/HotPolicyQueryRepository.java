package com.example.youthy.wonyeong2.repository;

import com.example.youthy.YouthPolicy;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HotPolicyQueryRepository extends JpaRepository<YouthPolicy, String> { // <- String으로 수정!

    // viewCount DESC 로 상위 N개
    List<YouthPolicy> findAllByOrderByViewCountDesc(Pageable pageable);
}
