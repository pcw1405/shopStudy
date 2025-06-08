package com.shopClone.service;


import com.shopClone.constant.PermissionType;
import com.shopClone.entity.Employee;
import com.shopClone.entity.Member;
import com.shopClone.entity.Post;
import com.shopClone.entity.PostPermission;
import com.shopClone.repository.PostPermissionRepository;
import com.shopClone.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostPermissionRepository postPermissionRepository;

    public List<Post> findReadablePosts(Member member) {
        Employee employee = member.getEmployee();
        List<PostPermission> permissions = postPermissionRepository.findByEmpOrTeamWithPermission(
                employee, employee.getTeam(), PermissionType.VIEW);

        return permissions.stream()
                .map(PostPermission::getPost)
                .distinct()
                .collect(Collectors.toList());
    }

    public boolean canEdit(Post post, Employee employee) {
        return postPermissionRepository.findByEmpOrTeamWithPermission(
                        employee, employee.getTeam(), PermissionType.EDIT)
                .stream()
                .anyMatch(p -> p.getPost().getId().equals(post.getId()));
    }

}
