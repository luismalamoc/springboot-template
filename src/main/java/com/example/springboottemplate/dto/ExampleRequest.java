package com.example.springboottemplate.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Example request DTO for API calls
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExampleRequest {
    private String name;
    private String description;
}
