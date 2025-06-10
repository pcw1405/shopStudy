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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
            return "error/403";
        }

        model.addAttribute("post", post);
        model.addAttribute("canEdit", postService.canEdit(post, member.getEmployee())); // <- 추가

        return "posts/detail";
    }

    /// 수정기능을 위한 페이지
    // 수정 폼 이동
    @GetMapping("/posts/edit/{id}")
    public String editForm(@PathVariable Long id, Model model, Principal principal) {
        if (principal == null) return "redirect:/members/login";

        String email = principal.getName();
        Member member = memberRepository.findByEmail(email);
        Post post = postService.findPostById(id);

        if (!postService.canEdit(post, member.getEmployee())) {
            return "error/403";
        }

        model.addAttribute("canEdit", postService.canEdit(post, member.getEmployee())); // 🔥 이 줄 추가!
        model.addAttribute("post", post);
        return "posts/edit"; // 별도 수정 페이지로 이동 시
    }

    // 수정 처리
    @PostMapping("/posts/update/{id}")
    public String updatePost(@PathVariable Long id,
                             @RequestParam String title,
                             @RequestParam String content,
                             Principal principal) {
        if (principal == null) return "redirect:/members/login";

        String email = principal.getName();
        Member member = memberRepository.findByEmail(email);
        postService.updatePost(id, title, content, member.getEmployee());

        return "redirect:/posts/" + id;
    }


}