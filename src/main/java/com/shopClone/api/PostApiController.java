package com.shopClone.api;

import com.shopClone.api.dto.BoardDto;
import com.shopClone.api.dto.PostListDto;
import com.shopClone.entity.BoardType;
import com.shopClone.entity.Member;
import com.shopClone.entity.Post;
import com.shopClone.repository.BoardTypeRepository;
import com.shopClone.repository.MemberRepository;
import com.shopClone.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PostApiController {

    private final BoardTypeRepository boardTypeRepository;
    private final MemberRepository memberRepository;
    private final PostService postService;

    @GetMapping("/boards")
    public List<BoardDto> boards(Principal principal) {
        if (principal == null) throw new UnauthorizedException();
        List<BoardType> boards = boardTypeRepository.findAll();
        return boards.stream().map(BoardDto::from).toList();
    }

    @GetMapping("/posts")
    public List<PostListDto> posts(@RequestParam(required = false) Long boardId, Principal principal) {
        System.out.println("[API] /api/posts called. boardId=" + boardId);
        if (principal == null) throw new UnauthorizedException();

        // boardId 없으면 "첫 게시판" 기본값
        if (boardId == null) {
            BoardType first = boardTypeRepository.findAll()
                    .stream()
                    .findFirst()
                    .orElseThrow(NoBoardException::new);
            boardId = first.getId();
        }

        Member member = memberRepository.findByEmail(principal.getName());
        List<Post> posts = postService.findReadablePosts(member, boardId);
        return posts.stream().map(PostListDto::from).toList();
    }
    @ResponseStatus(HttpStatus.NOT_FOUND)
    static class NoBoardException extends RuntimeException {}

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    static class UnauthorizedException extends RuntimeException {}
}