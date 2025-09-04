// src/main/java/com/example/youthy/domain/Member.java
package com.example.youthy.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@Builder
@NoArgsConstructor @AllArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long kakaoId;

    private String username;

    private String email;

    /** 서버측 토큰 무효화를 위한 버전 */
    @Builder.Default              // ✅ 빌더 사용 시에도 기본값 유지
    @Column(nullable = false)
    private int tokenVersion = 0;

    public void bumpTokenVersion() {
        this.tokenVersion++;
    }
}
