package com.shopClone.controller;

import com.shopClone.entity.BoardType;
import com.shopClone.entity.Member;
import com.shopClone.entity.Post;
import com.shopClone.repository.BoardTypeRepository;
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
    private final BoardTypeRepository boardTypeRepository;
//    @GetMapping("/posts")
//    public String listPosts(@RequestParam(required = false) Long boardId,
//                            Model model,
//                            Principal principal) {
//        if (principal == null) {
//            return "redirect:/members/login";
//        }
//        String email = principal.getName(); // 로그인한 사용자 email
//        Member member = memberRepository.findByEmail(email);
////        List<Post> posts = postService.findReadablePosts(member);
//        List<Post> posts = postService.findReadablePosts(member, boardId);
//
//        model.addAttribute("posts", posts);
//
//        model.addAttribute("currentBoardId", boardId);
//        return "posts/list";
//    }

    @GetMapping("/posts")
    public String listPosts(
            @RequestParam(required = false) Long boardId,
            Model model,
            Principal principal
    ) {
        if (principal == null) {
            return "redirect:/members/login";
        }

        String email = principal.getName();
        Member member = memberRepository.findByEmail(email);

        // 1) 탭용: 게시판 목록을 모델에 담기
        List<BoardType> boards = boardTypeRepository.findAll();
        model.addAttribute("boards", boards);

        // 2) 선택 boardId가 없으면 첫 게시판을 기본값으로
        Long selectedBoardId = (boardId != null)
                ? boardId
                : boards.stream().findFirst()
                .map(BoardType::getId)
                .orElseThrow(() -> new IllegalStateException("등록된 게시판이 없습니다."));

        model.addAttribute("currentBoardId", selectedBoardId);

        // 3) 핵심: 선택된 boardId로 조회
        List<Post> posts = postService.findReadablePosts(member, selectedBoardId);
        model.addAttribute("posts", posts);

        return "posts/list";
    }

    @GetMapping("/posts/{id}")
    public String viewPost(@PathVariable Long id,
                           @RequestParam(required = false) Long boardId,
                           Model model,
                           Principal principal) {

        if (principal == null) return "redirect:/members/login";
        Member member = memberRepository.findByEmail(principal.getName());
        Post post = postService.findPostById(id);

        if (!postService.canView(post, member)) return "error/403";

        model.addAttribute("post", post);
        model.addAttribute("canEdit", postService.canEdit(post, member));
        model.addAttribute("returnBoardId", (boardId != null) ? boardId : post.getBoardType().getId());

        return "posts/detail";
    }

    /// 수정기능을 위한 페이지
    // 수정 폼 이동
    @GetMapping("/posts/edit/{id}")
    public String editForm(@PathVariable Long id,
                           @RequestParam(required = false) Long boardId,
                           Model model,
                           Principal principal) {

        if (principal == null) return "redirect:/members/login";
        Member member = memberRepository.findByEmail(principal.getName());
        Post post = postService.findPostById(id);

        if (!postService.canEdit(post, member)) return "error/403";

        model.addAttribute("post", post);
        model.addAttribute("returnBoardId", (boardId != null) ? boardId : post.getBoardType().getId());

        return "posts/edit";
    }

    // 수정 처리
    @PostMapping("/posts/update/{id}")
    public String updatePost(@PathVariable Long id,
                             @RequestParam String title,
                             @RequestParam String content,
                             @RequestParam(required = false) Long boardId,
                             Principal principal) {

        if (principal == null) return "redirect:/members/login";
        Member member = memberRepository.findByEmail(principal.getName());
        postService.updatePost(id, title, content, member);

        Long returnBoardId = (boardId != null) ? boardId : postService.findPostById(id).getBoardType().getId();
        return "redirect:/posts/" + id + "?boardId=" + returnBoardId;
    }


}