package com.shopClone.controller;

import com.shopClone.entity.Member;
import com.shopClone.entity.Post;
import com.shopClone.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping("/posts")
    public String listPosts(Model model, @AuthenticationPrincipal Member member) {
        List<Post> posts = postService.findReadablePosts(member);
        model.addAttribute("posts", posts);
        return "posts/list";
    }
}