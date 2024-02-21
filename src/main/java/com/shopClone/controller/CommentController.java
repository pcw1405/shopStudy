package com.shopClone.controller;

import com.shopClone.dto.CommentDto;
import com.shopClone.entity.Member;
import com.shopClone.repository.MemberRepository;
import com.shopClone.service.CommentServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {

    @Autowired
    private CommentServiceImpl commentServiceImpl;
    @Autowired
    private MemberRepository memberRepository;
    @PostMapping("/addComment")
    public ResponseEntity<List<CommentDto>> addComment(@RequestBody CommentDto commentDto,Principal principal) {
        String email=principal.getName();
        System.out.println(commentDto.getItemId());
        System.out.println(commentDto.getContent());
//        commentDto.setCreatedAt(LocalDateTime.now());
//        System.out.println(commentDto.getCreatedAt());
        Member member=memberRepository.findByEmail(email);
        commentDto.setAuthorId(member.getId());
        System.out.println(commentDto.getAuthorId());
//        System.out.println(commentDto.getId());
     List<CommentDto> updatedComments = commentServiceImpl.addComment(commentDto);
        return new ResponseEntity<>(null, HttpStatus.OK);

        // pricipal이 바로 id를 가져올 수 있는 것은 아니다( load메서드 설정을 볼 때)
    }



}
