package com.example.youthy.wonyeong3.scrap.domain;

import com.example.youthy.domain.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "scrap",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_scrap_member_policy", columnNames = {"member_id", "policy_no"})
        },
        indexes = {
                @Index(name = "idx_scrap_member", columnList = "member_id")
        }
)
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Scrap {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "policy_no", nullable = false, length = 64)
    private String policyNo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_scrap_member"))
    private Member member;

    // 선택 필드가 있다면 여기에 추가 (title, memo 등)
}
