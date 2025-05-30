package com.example.springboottemplate.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for JSONPlaceholder Comment entity
 * https://jsonplaceholder.typicode.com/comments
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JsonPlaceholderCommentDto {
    private Long id;
    private Long postId;
    private String name;
    private String email;
    private String body;
}
