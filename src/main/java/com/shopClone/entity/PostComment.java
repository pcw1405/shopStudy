package com.shopClone.entity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "post_comment")
public class PostComment extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;  // 소속 게시글 (N:1)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee author;  // 댓글 작성자 (직원, N:1)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private PostComment parent;  // 상위 댓글 (부모 댓글, N:1 자기 자신 참조)

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    private List<PostComment> replies = new ArrayList<>();  // 하위 대댓글 목록

//    private LocalDateTime createdAt;
//    private LocalDateTime updatedAt;

    // Constructors, getters, setters ...
}