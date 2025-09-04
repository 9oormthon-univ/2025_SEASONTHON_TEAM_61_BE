package com.example.youthy;

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

    @Column(name = "zip_code", length = 10)
    private String zipCode;

    public PolicyResidence(YouthPolicy youthPolicy, String zipCode) {
        this.youthPolicy = youthPolicy;
        this.zipCode = zipCode;
    }
}
