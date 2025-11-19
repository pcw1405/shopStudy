package com.shopClone.repository;

import com.shopClone.constant.PermissionType;
import com.shopClone.entity.Employee;
import com.shopClone.entity.PostPermission;
import com.shopClone.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostPermissionRepository extends JpaRepository<PostPermission, Long> {

    /** 직원 + 권한으로 조회 */
    List<PostPermission> findByEmployeeAndPermission(Employee employee,
                                                     PermissionType permission);

    /** 직원이나 팀이 특정 권한을 가진 PostPermission 목록 */
    @Query(
            "select pp " +
                    "from PostPermission pp " +
                    "where (pp.employee = :employee or pp.team = :team) " +
                    "  and pp.permission = :permission"
    )
    List<PostPermission> findByEmpOrTeamWithPermission(@Param("employee") Employee employee,
                                                       @Param("team") Team team,
                                                       @Param("permission") PermissionType permission);

    /** 직원 단위 존재 여부 확인 */
    boolean existsByPostIdAndPermissionAndEmployeeId(Long postId,
                                                     PermissionType permission,
                                                     Long employeeId);

    /** 팀 단위 존재 여부 확인 */
    boolean existsByPostIdAndPermissionAndTeamId(Long postId,
                                                 PermissionType permission,
                                                 Long teamId);
}