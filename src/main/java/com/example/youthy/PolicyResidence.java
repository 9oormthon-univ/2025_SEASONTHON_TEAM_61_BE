package com.example.youthy;

import com.example.youthy.chungheon2.Region;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PolicyResidence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_no")
    private YouthPolicy youthPolicy;

    // ✅ String zipCode 대신 Region 엔티티를 참조하도록 변경
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_code")
    private Region region;

    public PolicyResidence(YouthPolicy youthPolicy, Region region) {
        this.youthPolicy = youthPolicy;
        this.region = region;
    }
}