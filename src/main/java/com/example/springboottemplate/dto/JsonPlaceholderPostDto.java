package com.example.springboottemplate.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for JSONPlaceholder Post entity
 * https://jsonplaceholder.typicode.com/posts
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JsonPlaceholderPostDto {
    private Long id;
    private Long userId;
    private String title;
    private String body;
}
