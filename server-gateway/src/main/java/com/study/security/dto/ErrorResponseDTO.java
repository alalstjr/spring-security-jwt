package com.study.security.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorResponseDTO {
    private String error;
    private String message;
    private String path;
    private String requestId;
    private int status;
    private String timestamp;
}