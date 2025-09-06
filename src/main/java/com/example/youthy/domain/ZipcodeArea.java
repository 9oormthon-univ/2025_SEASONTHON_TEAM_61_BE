package com.example.youthy.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "zipcode_area",
        indexes = {
                @Index(name = "ix_za_region", columnList = "region"),
                @Index(name = "ix_za_sub", columnList = "subregion"),
                @Index(name = "ix_za_region_sub", columnList = "region,subregion")
        }
)
@Getter @Setter @NoArgsConstructor
public class ZipcodeArea {

    @Id
    @Column(length = 10)
    private String zipCode;     // PK = 우편번호

    @Column(length = 50, nullable = false)
    private String region;      // 시/도

    @Column(length = 50, nullable = false)
    private String subregion;   // 구/군

    @Column(length = 100)
    private String dong;        // (선택) 동/읍/면
}
