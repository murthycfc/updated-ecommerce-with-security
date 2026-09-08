package com.dependency.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponse {
    private UUID userId;
    private String name;
    private String email;
    private String phoneNumber;
    private String role;
    private String message;
}
