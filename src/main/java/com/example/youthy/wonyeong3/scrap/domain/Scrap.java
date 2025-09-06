package com.example.youthy.wonyeong3.scrap.domain;

import com.example.youthy.YouthPolicy;
import com.example.youthy.domain.Member;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "scrap",
        uniqueConstraints = @UniqueConstraint(name = "uk_scrap_member_policy",
                columnNames = {"member_id","policy_no"}))
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Scrap {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // surrogate PK

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "policy_no", nullable = false) // YouthPolicy의 PK(정책번호)에 FK
    private YouthPolicy policy;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}
