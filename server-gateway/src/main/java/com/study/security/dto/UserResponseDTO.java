package com.study.security.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseCookie;

import java.util.List;

@Getter
@Setter
@Builder
public class UserResponseDTO {
    private String username;
    private String roleName;

    // 쿠키에 넣어 줄 토큰 정보를 쓸 때 사용
    private List<ResponseCookie> authCookie;
}
