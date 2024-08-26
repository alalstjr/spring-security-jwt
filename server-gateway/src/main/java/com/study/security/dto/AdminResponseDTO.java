package com.study.security.dto;

import java.util.Set;

public record AdminResponseDTO(String username, Set<String> roles) {
}
