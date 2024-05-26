package com.example.demo.dto;

import java.util.Set;

public record ProfileResponseDto(String username, Set<String> roles) {
}