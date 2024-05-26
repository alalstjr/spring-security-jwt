package com.example.demo.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserInfoRequestDto {

    private String username;

    private String password;

    private String role;
}