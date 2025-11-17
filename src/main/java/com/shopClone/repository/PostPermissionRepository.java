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

    List<PostPermission> findByEmployeeAndPermissionType(Employee employee, PermissionType permissionType);

    @Query("SELECT pp FROM PostPermission pp WHERE (pp.employee = :employee OR pp.team = :team) AND pp.permissionType = :permission")
    List<PostPermission> findByEmpOrTeamWithPermission(@Param("employee") Employee employee,
                                                       @Param("team") Team team,
                                                       @Param("permission") PermissionType permission);

    boolean existsByPostIdAndPermissionAndEmployeeId(Long postId, com.shopClone.constant.PermissionType permission, Long employeeId);
    boolean existsByPostIdAndPermissionAndTeamId(Long postId, com.shopClone.constant.PermissionType permission, Long teamId);
}
