// src/main/java/com/example/youthy/domain/Member.java
package com.example.youthy.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "member", indexes = {
        @Index(name = "idx_member_kakao_id", columnList = "kakaoId", unique = true)
})
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ✅ 카카오 고유 ID(반드시 unique)
    @Column(nullable = false, unique = true)
    private Long kakaoId;

    // 동의 안 받으면 null 가능
    private String email;

    // 카카오 닉네임
    private String username;
}
