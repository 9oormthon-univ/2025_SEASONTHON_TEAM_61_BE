package com.example.youthy.wonyeong.repository;

import com.example.youthy.domain.ZipcodeArea;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ZipcodeAreaRepository extends JpaRepository<ZipcodeArea, String> {

    // 시/도 전체 우편번호
    List<ZipcodeArea> findAllByRegion(String region);

    // 시/도 + 구/군 우편번호
    List<ZipcodeArea> findAllByRegionAndSubregion(String region, String subregion);
}
