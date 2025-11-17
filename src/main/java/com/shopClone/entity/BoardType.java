package com.shopClone.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "board_type")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardType extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
//    @Column(nullable = false, length = 100)
    private String name; // "공지사항", "자유게시판", "팀게시판" 등

//    @Column(length = 500)
    private String description;
//    private String icon; // 아이콘 클래스명
//    @Column(name = "sort_order")
//    @Builder.Default
//    private Integer sortOrder = 0;

    // 이 타입의 게시글들
    @OneToMany(mappedBy = "boardType", fetch = FetchType.LAZY)
    private List<Post> posts = new ArrayList<>(); // 해당 분류에 속한 게시글 목록
    // 게시판별 기본 권한 설정
    @OneToMany(mappedBy = "boardType", fetch = FetchType.LAZY)
    private List<BoardPermission> permissions = new ArrayList<>();  // 이 게시판에 대한 권한 목록

//    // 2025년 7월 26일 추가 createdAt, updatedAT
//    private LocalDateTime createdAt;
//    private LocalDateTime updatedAt;

}
