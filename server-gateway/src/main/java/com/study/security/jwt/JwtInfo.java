package com.study.security.jwt;

import lombok.Builder;
import lombok.Data;

/**
 * JWToken Response 값을 저장
 */
@Data
@Builder
public class JwtInfo {
    private String token;
    private String tokenExpiredAt;
    private String refreshToken;
    private String refreshTokenExpiredAt;
}
