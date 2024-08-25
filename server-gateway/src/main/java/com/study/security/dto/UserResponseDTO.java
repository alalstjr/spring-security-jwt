package com.study.security.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserResponseDTO {
    private String username;
    private String roleName;
}
