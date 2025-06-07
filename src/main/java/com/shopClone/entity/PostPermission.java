package com.shopClone.entity;


import com.shopClone.constant.PermissionType;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "post_permission")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostPermission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @Enumerated(EnumType.STRING)
    private PermissionType permissionType;
}
