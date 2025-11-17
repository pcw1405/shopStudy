package com.shopClone.repository;

import com.shopClone.constant.PermissionType;
import com.shopClone.entity.BoardPermission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardPermissionRepository extends JpaRepository<BoardPermission ,Long> {
    boolean existByBoardTypeId(Long boardTypeId); // 권한 레코드 유무로 '공개 보드' 판별
    boolean existsByBoardTypeIdAndEmployeeIdAndPermission(Long boardTypeId, Long employeeId, PermissionType permission);
    boolean existsByBoardTypeIdAndTeamIdAndPermission(Long boardTypeId, Long teamId, PermissionType permission);

    // 수정 시 깔끔하게 재설정할 때 필요
    long deleteByPostId(Long postId);

    long countByBoardTypeIdAndPermission(Long boardTypeId, com.shopClone.constant.PermissionType permission);

}
