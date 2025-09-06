package com.example.youthy.repository;

import com.example.youthy.PolicyResidence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface PolicyResidenceRepository extends JpaRepository<PolicyResidence, Long> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "DELETE FROM policy_residence WHERE policy_no = :policyNo", nativeQuery = true)
    int deleteByPolicyNo(String policyNo);
}
