package com.example.youthy.chungheon2;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 지역 코드와 지역 이름을 매핑하는 '지역 사전' 엔티티입니다.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Region {

    @Id
    @Column(name = "region_code", length = 10)
    private String code; // 지역 코드 (예: "11000") - Primary Key

    @Column(name = "region_name", nullable = false)
    private String name; // 지역 이름 (예: "서울특별시")
}
