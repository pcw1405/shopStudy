package com.shopClone.service;

import com.shopClone.dto.CommentDto;
import com.shopClone.entity.Comment;
import com.shopClone.entity.Item;
import com.shopClone.entity.Member;
import com.shopClone.repository.CommentRepository;
import com.shopClone.repository.ItemRepository;
import com.shopClone.repository.MemberRepository;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import javassist.NotFoundException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService{
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    @Override
    public List<CommentDto> addComment(CommentDto commentDto) {
        // CommentDto에서 필요한 정보를 이용해 Comment 엔티티를 생성
        Comment comment = new Comment();
        comment.setContent(commentDto.getContent());

        // 작성자 정보 설정
        try {
            Member author = memberRepository.findById(commentDto.getAuthorId())
                    .orElseThrow(() -> new NotFoundException("Member not found with id: " + commentDto.getAuthorId()));
            comment.setAuthor(author);
        } catch (NotFoundException e) {
            // NotFoundException을 처리하는 코드 추가
            e.printStackTrace(); // 또는 로깅 등의 다른 처리 방법을 선택할 수 있음
        }

        // 댓글이 달릴 상품 정보 설정
        try {
            Item item = itemRepository.findById(commentDto.getItemId())
                    .orElseThrow(() -> new NotFoundException("Item not found with id: " + commentDto.getItemId()));
            comment.setItem(item);
        } catch (NotFoundException e) {
            // NotFoundException을 처리하는 코드 추가
            e.printStackTrace(); // 또는 로깅 등의 다른 처리 방법을 선택할 수 있음
        }

        // 댓글 저장
        commentRepository.save(comment);

        // 댓글 목록 조회 및 반환
        List<CommentDto> updatedComments = getCommentsByItemId(commentDto.getItemId());
        return updatedComments;
    }

    public List<CommentDto> getCommentsByItemId(Long itemId) {
        List<Comment> comments = commentRepository.findByItemId(itemId);
        List<CommentDto> commentDtoList = new ArrayList<>();

        for (Comment comment : comments) {
            CommentDto commentDto = new CommentDto();
            commentDto.setId(comment.getId());
//            System.out.println(comment.getId());
            commentDto.setContent(comment.getContent());
//            System.out.println(comment.getContent());
            commentDto.setAuthorId(comment.getAuthor().getId());
            commentDto.setCreatedAt(comment.getCreatedAt());
            commentDto.setItemId(comment.getItem().getId());

            commentDtoList.add(commentDto);
        }

        return commentDtoList;
    }
}
