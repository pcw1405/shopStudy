package com.shopClone.controller;

import com.shopClone.entity.Member;
import com.shopClone.entity.Post;
import com.shopClone.repository.MemberRepository;
import com.shopClone.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final MemberRepository memberRepository;

    @GetMapping("/posts")
    public String listPosts(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/members/login";
        }
        String email = principal.getName(); // 로그인한 사용자 email
        Member member = memberRepository.findByEmail(email);
        List<Post> posts = postService.findReadablePosts(member);
        model.addAttribute("posts", posts);
        return "posts/list";
    }

    @GetMapping("/posts/{id}")
    public String viewPost(@PathVariable Long id, Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/members/login";
        }

        String email = principal.getName();
        Member member = memberRepository.findByEmail(email);
        Post post = postService.findPostById(id);

        if (!postService.canView(post, member.getEmployee())) {
            return "error/403"; // 권한 없을 때
        }

        model.addAttribute("post", post);
        return "posts/detail";
    }
}