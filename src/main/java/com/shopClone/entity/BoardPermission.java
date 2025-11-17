package com.shopClone.entity;

import com.shopClone.constant.PermissionType;

import javax.persistence.*;

@Entity
@Table(name = "board_permission")
public class BoardPermission extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private PermissionType permission;  // 권한 종류
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_type_id")
    private BoardType boardType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;  // 개별 사용자 권한 (optional)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;  // 팀 단위 권한 (optional)

    //    @Enumerated(EnumType.STRING)
//    private Role requiredRole; // 최소 요구 역할

//    private LocalDateTime createdAt;
//    private LocalDateTime updatedAt;
}
