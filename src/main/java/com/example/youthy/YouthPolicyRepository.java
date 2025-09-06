package com.example.youthy;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * YouthPolicy 엔티티에 대한 데이터 접근을 처리하는 JpaRepository 인터페이스입니다.
 * 복잡한 동적 검색 쿼리를 위해 JpaSpecificationExecutor를 상속받도록 확장되었습니다.
 */
@Repository
public interface YouthPolicyRepository extends JpaRepository<YouthPolicy, String>, JpaSpecificationExecutor<YouthPolicy> {

    /**
     * 정책번호(policyNo)로 단건 조회.
     * PK가 policyNo가 아니더라도 확실하게 매칭하기 위해 별도 메서드 제공.
     */
    Optional<YouthPolicy> findByPolicyNo(String policyNo);

    /**
     * 정책번호 존재 여부 확인.
     */
    boolean existsByPolicyNo(String policyNo);

    /**
     * 정책 이름(policyName)에 특정 키워드가 포함된 정책 목록을 페이징하여 조회합니다.
     */
    Page<YouthPolicy> findByPolicyNameContaining(String keyword, Pageable pageable);

    /**
     * 여러 정책 분야에 해당하는 정책 목록을 조회합니다.
     * @param fields 정책 분야 리스트
     * @param pageable 페이징 정보
     * @return 페이징된 정책 목록
     */
    Page<YouthPolicy> findByPolicyFieldIn(List<String> fields, Pageable pageable);

    /**
     * 정책 분야(policyField)를 기준으로 정책 목록을 페이징하여 조회합니다.
     * @param policyField 검색할 정책 분야 (예: "일자리", "복지문화")
     * @param pageable 페이징 정보 (page, size)
     * @return 페이징된 정책 엔티티 목록
     */
    Page<YouthPolicy> findByPolicyField(String policyField, Pageable pageable);
}
