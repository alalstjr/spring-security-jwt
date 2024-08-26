package com.study.security.jwt;

/**
 * 로그인 관련 공통 용어
 */
public enum AuthKeyWord {
    BEARER("Bearer "),
    ROLE_KEY("roles"),
    ACCESS_TOKEN("AccessToken"),
    REFRESH_TOKEN("RefreshToken"),
    ;

    public final String value;

    AuthKeyWord(String value) {
        this.value = value;
    }
}
