package com.example.youthy.wonyeong.domain;

import com.example.youthy.YouthPolicy;            // ← 기존 엔티티 import
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PolicyRepository extends JpaRepository<YouthPolicy, Long> {
    List<YouthPolicy> findBySigungu(Sigungu sigungu);
}
