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
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;
    /// 7월 26일 추가
    private int viewCount = 0;
    private int replyCount = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_type_id")
    private BoardType boardType;  // 게시판 분류 (N:1)

    // 작성자 (직원)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee author;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
    private List<PostComment> comments = new ArrayList<>();  // 게시글의 댓글들

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
    private List<PostPermission> permissions = new ArrayList<>();  // 게시글별 지정 권한 목록
    // 열람 권한이 있는 팀들
//    @ManyToMany
//    @JoinTable(
//            name = "post_team_access",
//            joinColumns = @JoinColumn(name = "post_id"),
//            inverseJoinColumns = @JoinColumn(name = "team_id")
//    )
//    private List<Team> readableTeams = new ArrayList<>();
//
//    // 열람 권한이 있는 개별 직원들
//    @ManyToMany
//    @JoinTable(
//            name = "post_employee_access",
//            joinColumns = @JoinColumn(name = "post_id"),
//            inverseJoinColumns = @JoinColumn(name = "employee_id")
//    )
//    private List<Employee> readableEmployees = new ArrayList<>();
}