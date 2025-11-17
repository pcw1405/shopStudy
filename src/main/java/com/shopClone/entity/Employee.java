package com.shopClone.entity;

import com.shopClone.constant.EmployeeLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "employee")
@Getter
@Setter
@NoArgsConstructor
public class Employee extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;

    // N:1 팀 소속 2025년 7월 26일 (fetch = FetchType.LAZY) 추가
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @OneToOne(mappedBy = "employee", fetch = FetchType.LAZY)
    private Member member;  // 1:1 연결된 회원 계정 (Member) 2025년 7월 26일에 추가

    //선택적 초기 설계 유연성 확보 (nullable = false) 제거 7월 26일
    @Enumerated(EnumType.STRING)
    private EmployeeLevel level; // 직원 직급 (예: JUNIOR, SENIOR)

    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY)
    private List<Post> posts = new ArrayList<>();  // 직원이 작성한 게시글들

    // Constructors, getters, setters ...
}
