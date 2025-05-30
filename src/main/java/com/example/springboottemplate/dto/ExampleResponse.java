package com.example.springboottemplate.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Example response DTO for API calls
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExampleResponse {
    private String id;
    private String name;
    private String description;
    private String createdAt;
}
