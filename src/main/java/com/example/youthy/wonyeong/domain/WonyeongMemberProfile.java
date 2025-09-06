package com.example.youthy.wonyeong.domain;

import com.example.youthy.domain.Member;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "wonyeong_member_profile",
        uniqueConstraints = @UniqueConstraint(name = "uk_profile_member", columnNames = "member_id")
)
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WonyeongMemberProfile {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 원본 Member 와 1:1 매핑 (변경 없이 참조) */
    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_profile_member"))
    private Member member;

    /** 연령대 (한글 직렬화/역직렬화는 AgeGroup enum의 @JsonCreator/@JsonValue로 처리) */
    @Enumerated(EnumType.STRING)
    @Column(name = "age_group", length = 20, nullable = false)
    private AgeGroup ageGroup;

    /** 관심 카테고리(복수, 한글 → Category enum 매핑) */
    @ElementCollection(targetClass = Category.class)
    @CollectionTable(
            name = "wonyeong_member_interest",
            joinColumns = @JoinColumn(name = "profile_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "category", length = 50, nullable = false)
    @Builder.Default
    private Set<Category> interestedCategories = new HashSet<>();
}
