package com.shopClone.repository;

import com.shopClone.entity.Employee;
import com.shopClone.entity.Post;
import com.shopClone.entity.Team;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    @EntityGraph(attributePaths = {"author","boardType"})
    Page<Post> findByBoardTypeId(Long boardTypeId, Pageable pageable);

    /**
     * 권한 반영 목록 (EXISTS 기반)
     * - boardPublic = true(공개): 게시글에 PostPermission이 없으면 모두 read 가능
     * - boardPublic = false(제한): 게시글에 PostPermission 없으면 BoardPermission(READ) 만족해야 read 가능
     * - 게시글에 PostPermission(READ) 있으면 해당 팀/직원 + 작성자만 read 가능
     */
    @Query("""
select p
from Post p
where p.boardType.id = :boardId
  and (
    (
      -- case1: 글에 PostPermission(READ)이 없는 경우: 보드 규칙 상속
      not exists (
        select 1
        from PostPermission pp
        where pp.post.id = p.id
          and pp.permission = com.shopClone.constant.PermissionType.READ
      )
      and (
           :boardPublic = true
           or exists (
                select 1
                from BoardPermission bp
                where bp.boardType.id = p.boardType.id
                  and bp.permission = com.shopClone.constant.PermissionType.READ
                  and (
                        (bp.employee.id = :empId)
                     or (bp.team.id = :teamId)
                  )
           )
      )
    )
    or
    (
      -- case2: 글에 PostPermission(READ)이 있는 경우: 그 명단 또는 작성자
      exists (
        select 1
        from PostPermission pp2
        where pp2.post.id = p.id
          and pp2.permission = com.shopClone.constant.PermissionType.READ
          and (
                (pp2.employee.id = :empId)
             or (pp2.team.id = :teamId)
          )
      )
      or p.author.id = :empId
    )
  )
""")
    Page<Post> findVisiblePosts(@Param("boardId") Long boardId,
                                @Param("empId") Long empId,
                                @Param("teamId") Long teamId,
                                @Param("boardPublic") boolean boardPublic,
                                Pageable pageable);
//    /** 권한 반영 목록 (EXISTS 기반) — 엔티티 필드명에 맞게 작성 */
//    @Query("""
//    select p from Post p
//    where p.boardType.id = :boardId
//      and (
//        (
//          not exists (select 1 from PostPermission pp where pp.post.id = p.id)
//          and (
//            :boardPublic = true
//            or exists (select 1 from BoardPermission bp
//                       where bp.boardType.id = p.boardType.id
//                         and bp.permission = com.shopClone.constant.PermissionType.READ
//                         and (
//                           (bp.employee.id = :empId)
//                           or (bp.team.id = :teamId)
//                         ))
//          )
//        )
//        or
//        (
//          exists (select 1 from PostPermission pp2
//                  where pp2.post.id = p.id
//                    and pp2.permission = com.shopClone.constant.PermissionType.READ
//                    and (
//                      (pp2.employee.id = :empId)
//                      or (pp2.team.id = :teamId)
//                    ))
//          or p.author.id = :empId
//        )
//      )
//    """)
//    Page<Post> findVisiblePosts(@Param("boardId") Long boardId,
//                                @Param("empId") Long empId,
//                                @Param("teamId") Long teamId,
//                                @Param("boardPublic") boolean boardPublic,
//                                Pageable pageable);
}