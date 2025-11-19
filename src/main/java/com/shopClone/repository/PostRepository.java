package com.shopClone.repository;

import com.shopClone.constant.PermissionType;
import com.shopClone.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long> {

    @EntityGraph(attributePaths = {"author", "boardType"})
    Page<Post> findByBoardTypeId(Long boardTypeId, Pageable pageable);

    /**
     * 권한 반영 목록 (EXISTS 기반)
     *
     * - boardPublic = true(공개):
     *     게시글에 PostPermission(READ)이 없으면 모두 read 가능
     * - boardPublic = false(제한):
     *     게시글에 PostPermission(READ)이 없으면
     *     BoardPermission(READ && (해당 직원 or 팀)) 만족해야 read 가능
     * - 게시글에 PostPermission(READ)이 있으면
     *     해당 팀/직원 또는 작성자만 read 가능
     * - 작성자는 PostPermission/BoardPermission과 상관없이 자신의 글은 항상 read 가능
     */
    @Query(
            "select p " +
                    "from Post p " +
                    "where p.boardType.id = :boardId " +
                    "  and ( " +
                    "    ( " +
                    "      not exists ( " +
                    "        select 1 " +
                    "        from PostPermission pp " +
                    "        where pp.post.id = p.id " +
                    "          and pp.permission = :readPermission " +   // ✅ permission 사용
                    "      ) " +
                    "      and ( " +
                    "           :boardPublic = true " +
                    "           or exists ( " +
                    "                select 1 " +
                    "                from BoardPermission bp " +
                    "                where bp.boardType.id = p.boardType.id " +
                    "                  and bp.permission = :readPermission " +
                    "                  and ( " +
                    "                        bp.employee.id = :empId " +
                    "                     or bp.team.id = :teamId " +
                    "                  ) " +
                    "           ) " +
                    "      ) " +
                    "    ) " +
                    "    or " +
                    "    ( " +
                    "      exists ( " +
                    "        select 1 " +
                    "        from PostPermission pp2 " +
                    "        where pp2.post.id = p.id " +
                    "          and pp2.permission = :readPermission " +  // ✅ permission 사용
                    "          and ( " +
                    "                pp2.employee.id = :empId " +
                    "             or pp2.team.id = :teamId " +
                    "          ) " +
                    "      ) " +
                    "      or p.author.id = :empId " +
                    "    ) " +
                    "  )"
    )
    Page<Post> findVisiblePosts(@Param("boardId") Long boardId,
                                @Param("empId") Long empId,
                                @Param("teamId") Long teamId,
                                @Param("boardPublic") boolean boardPublic,
                                @Param("readPermission") PermissionType readPermission,
                                Pageable pageable);
}