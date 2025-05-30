package com.example.springboottemplate.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for JSONPlaceholder User entity
 * https://jsonplaceholder.typicode.com/users
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JsonPlaceholderUserDto {
    private Long id;
    private String name;
    private String username;
    private String email;
    private Address address;
    private String phone;
    private String website;
    private Company company;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Address {
        private String street;
        private String suite;
        private String city;
        private String zipcode;
        private Geo geo;
        
        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Geo {
            private String lat;
            private String lng;
        }
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Company {
        private String name;
        private String catchPhrase;
        private String bs;
    }
}
