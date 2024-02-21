package com.shopClone.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CommentDto {
    private Long id;
    private String content;
    private Long authorId;
    private LocalDateTime createdAt;

    private Long itemId;
}
