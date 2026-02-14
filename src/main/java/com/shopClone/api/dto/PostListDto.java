package com.shopClone.api.dto;

import com.shopClone.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostListDto {
    private Long id;
    private String title;
    private String authorName;

    public static PostListDto from(Post p) {
        String author = (p.getAuthor() != null) ? p.getAuthor().getName() : "알 수 없음";
        return new PostListDto(p.getId(), p.getTitle(), author);
    }
}