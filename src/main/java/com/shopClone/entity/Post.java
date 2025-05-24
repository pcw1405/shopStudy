package com.shopClone.entity;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "post")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    // 작성자 (직원)
    @ManyToOne
    @JoinColumn(name = "author_id")
    private Employee author;

    // 열람 권한이 있는 팀들
    @ManyToMany
    @JoinTable(
            name = "post_team_access",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "team_id")
    )
    private List<Team> readableTeams = new ArrayList<>();

    // 열람 권한이 있는 개별 직원들
    @ManyToMany
    @JoinTable(
            name = "post_employee_access",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "employee_id")
    )
    private List<Employee> readableEmployees = new ArrayList<>();
}