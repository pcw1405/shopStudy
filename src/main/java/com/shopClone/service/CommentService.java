package com.shopClone.service;

import com.shopClone.dto.CommentDto;

import java.util.List;

public interface CommentService {
    List<CommentDto> addComment(CommentDto commentDto);

}
