package com.example.youthy;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * YouthPolicy 엔티티에 대한 데이터 접근을 처리하는 JpaRepository 인터페이스입니다.
 * 복잡한 동적 검색 쿼리를 위해 JpaSpecificationExecutor를 상속받도록 확장되었습니다.
 */
@Repository
public interface YouthPolicyRepository extends JpaRepository<YouthPolicy, String>, JpaSpecificationExecutor<YouthPolicy> {

    /**
     * 정책 이름(policyName)에 특정 키워드가 포함된 정책 목록을 페이징하여 조회합니다.
     */
    Page<YouthPolicy> findByPolicyNameContaining(String keyword, Pageable pageable);

    /**
     * 특정 연령이 지원 가능한 정책을 조회합니다.
     * (예: 25세 사용자는 minAge <= 25 이고 maxAge >= 25인 정책을 검색)
     * @param age 사용자의 나이
     * @return 페이징된 정책 목록
     */
    Page<YouthPolicy> findByMinAgeLessThanEqualAndMaxAgeGreaterThanEqual(Integer age, Pageable pageable);

    /**
     * 여러 정책 분야에 해당하는 정책 목록을 조회합니다.
     * @param fields 정책 분야 리스트
     * @param pageable 페이징 정보
     * @return 페이징된 정책 목록
     */
    Page<YouthPolicy> findByPolicyFieldIn(List<String> fields, Pageable pageable);

    // JpaRepository를 상속받았기 때문에 기본적인 CRUD 메서드는 이미 포함되어 있습니다.
    // (개발자 A는 saveAll()을 사용하여 외부 API 데이터를 일괄 저장할 수 있습니다.)

    // JpaSpecificationExecutor를 상속받았기 때문에, 개발자 B는 서비스 계층에서
    // 다양한 검색 조건(거주지역, 학력, 전공 등)을 조합한 동적 쿼리를 Specification으로 생성하여 사용할 수 있습니다.
    // 이는 수십 개의 findBy... 메서드를 만드는 것보다 훨씬 유연하고 효율적입니다.
}

