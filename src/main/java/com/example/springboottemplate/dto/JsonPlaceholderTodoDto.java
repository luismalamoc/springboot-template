package com.example.springboottemplate.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for JSONPlaceholder Todo entity
 * https://jsonplaceholder.typicode.com/todos
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JsonPlaceholderTodoDto {
    private Long id;
    private Long userId;
    private String title;
    private Boolean completed;
}
