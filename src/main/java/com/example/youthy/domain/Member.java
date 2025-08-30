package com.example.youthy.domain;

import jakarta.persistence.*;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor // 매개변수가 없는 기본 생성자 생성 -> 엔티티 클래스에 기본 생성자가 있어야 하기 때문에 사용
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long member_id;

    private String username;
}
