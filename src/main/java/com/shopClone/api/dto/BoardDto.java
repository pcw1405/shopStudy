package com.shopClone.api.dto;

import com.shopClone.entity.BoardType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BoardDto {
    private Long id;
    private String name;

    public static BoardDto from(BoardType b) {
        return new BoardDto(b.getId(), b.getName());
    }
}