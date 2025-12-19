package com.example.mymemories.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor  

public class AuthResponse {
    private String token;
    private String message;
    private Long id;
//    private String fullName;
    private String email;
    private String username;
    private boolean enabled;
    private String createdAt;
    private String updatedAt;
}