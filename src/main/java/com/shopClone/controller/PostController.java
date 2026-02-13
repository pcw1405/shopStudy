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

    @GetMapping("/posts/new")
    public String newPostForm(@RequestParam(required = false) Long boardId,
                              Model model,
                              Principal principal) {

        if (principal == null) {
            return "redirect:/members/login";
        }

        Member member = memberRepository.findByEmail(principal.getName());

        // 탭 유지
        List<BoardType> boards = boardTypeRepository.findAll();
        model.addAttribute("boards", boards);

        Long selectedBoardId = (boardId != null)
                ? boardId
                : boards.stream().findFirst()
                .map(BoardType::getId)
                .orElseThrow(() -> new IllegalStateException("등록된 게시판이 없습니다."));

        model.addAttribute("currentBoardId", selectedBoardId);

        // ✅ WRITE 권한 체크(없으면 403)
        if (!postService.canWriteBoard(selectedBoardId, member)) {
            return "error/403";
        }

        // 폼 기본값
        model.addAttribute("boardId", selectedBoardId);
        model.addAttribute("title", "");
        model.addAttribute("content", "");

        return "posts/new";
    }

    @PostMapping("/posts/create")
    public String createPost(@RequestParam Long boardId,
                             @RequestParam String title,
                             @RequestParam String content,
                             Model model,
                             Principal principal) {

        if (principal == null) {
            return "redirect:/members/login";
        }

        Member member = memberRepository.findByEmail(principal.getName());

        // ===== 입력 검증 =====
        String t = (title == null) ? "" : title.trim();
        String c = (content == null) ? "" : content.trim();

        if (t.isBlank() || t.length() > 100 || c.isBlank() || c.length() > 5000) {
            // 폼 재노출(탭 유지)
            List<BoardType> boards = boardTypeRepository.findAll();
            model.addAttribute("boards", boards);
            model.addAttribute("currentBoardId", boardId);

            model.addAttribute("boardId", boardId);
            model.addAttribute("title", t);
            model.addAttribute("content", c);
            model.addAttribute("errorMessage", "제목(1~100), 내용(1~5000)을 확인해주세요.");

            return "posts/new";
        }

        try {
            postService.createPost(boardId, t, c, member);
        } catch (SecurityException e) {
            return "error/403";
        } catch (IllegalArgumentException e) {
            // 게시판 없음(404 성격)
            throw e;
        }

        return "redirect:/posts?boardId=" + boardId;
    }


}