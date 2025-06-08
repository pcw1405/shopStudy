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

    // 상세페이지 열람을 권한있는 사람만 할 수 있게 해야함
    public boolean canView(Post post, Employee employee) {
        // 작성자인 경우 무조건 열람 가능
        if (post.getAuthor().getId().equals(employee.getId())) {
            return true;
        }

        // 작성자가 아닌 경우, 권한이 있는지 확인
        return postPermissionRepository.findByEmpOrTeamWithPermission(
                        employee, employee.getTeam(), PermissionType.VIEW)
                .stream()
                .anyMatch(p -> p.getPost().getId().equals(post.getId()));
    }

    // 게시물을 찾기위해서
    public Post findPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다. id=" + id));
    }

    public boolean canEdit(Post post, Employee employee) {
        return postPermissionRepository.findByEmpOrTeamWithPermission(
                        employee, employee.getTeam(), PermissionType.EDIT)
                .stream()
                .anyMatch(p -> p.getPost().getId().equals(post.getId()));
    }

}
