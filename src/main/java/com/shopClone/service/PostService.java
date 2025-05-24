package com.shopClone.service;


import com.shopClone.entity.Employee;
import com.shopClone.entity.Member;
import com.shopClone.entity.Post;
import com.shopClone.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    public List<Post> findReadablePosts(Member member) {
        Employee employee = member.getEmployee(); // Member → Employee 연결돼 있다고 가정

        return postRepository.findAll().stream()
                .filter(post -> canRead(post, employee))
                .collect(Collectors.toList());
    }
    public boolean canView(Post post, Employee viewer) {
        return post.getAuthor().getId().equals(viewer.getId()) ||
                post.getReadableEmployees().contains(viewer) ||
                post.getReadableTeams().contains(viewer.getTeam());
    }
    private boolean canRead(Post post, Employee employee) {
        return post.getAuthor().equals(employee) ||
                post.getReadableEmployees().contains(employee) ||
                post.getReadableTeams().contains(employee.getTeam());
    }
}
