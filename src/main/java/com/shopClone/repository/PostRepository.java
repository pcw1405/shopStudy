package com.shopClone.repository;

import com.shopClone.entity.Employee;
import com.shopClone.entity.Post;
import com.shopClone.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    // 현재 로그인한 직원 기준 열람 가능한 글 목록
    @Query("SELECT p FROM Post p WHERE " +
            "p.author = :employee OR " +
            ":employee MEMBER OF p.readableEmployees OR " +
            "p.team IN :teams")
    List<Post> findAccessiblePosts(@Param("employee") Employee employee,
                                   @Param("teams") List<Team> teams);
}