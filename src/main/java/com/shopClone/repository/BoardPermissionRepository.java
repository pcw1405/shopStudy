package com.shopClone.repository;

import com.shopClone.constant.PermissionType;
import com.shopClone.entity.BoardPermission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardPermissionRepository extends JpaRepository<BoardPermission ,Long> {

    // ✅ 게시판 + 권한별 개수 (공개판 여부 판단용)
    long countByBoardType_IdAndPermission(Long boardTypeId,
                                          PermissionType permission);

    // ✅ 특정 게시판에서, 특정 직원이 특정 권한을 갖고 있는지
    boolean existsByBoardType_IdAndEmployee_IdAndPermission(Long boardTypeId,
                                                            Long employeeId,
                                                            PermissionType permission);

    // ✅ 특정 게시판에서, 특정 팀이 특정 권한을 갖고 있는지
    boolean existsByBoardType_IdAndTeam_IdAndPermission(Long boardTypeId,
                                                        Long teamId,
                                                        PermissionType permission);

    // ❌ 이건 BoardPermission 엔티티에 post가 없으니까 삭제하는 게 맞음
    // long deleteByPostId(Long postId);
}