package com.example.youthy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * policy_category 테이블을 native 쿼리로 조작하기 위한 레포지토리.
 * 별도 엔티티 없이 더미 타입으로 JpaRepository를 만족시킵니다.
 */
public interface PolicyCategoryRepository extends JpaRepository<PolicyCategoryRepository.Dummy, Long> {

    class Dummy {}

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "DELETE FROM policy_category WHERE policy_no = :policyNo", nativeQuery = true)
    int deleteByPolicyNo(String policyNo);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "INSERT INTO policy_category (policy_no, category_id) VALUES (:policyNo, :categoryId)", nativeQuery = true)
    int insertOne(String policyNo, Long categoryId);
}
