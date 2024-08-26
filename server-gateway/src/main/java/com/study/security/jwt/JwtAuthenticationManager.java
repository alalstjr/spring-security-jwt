package com.study.security.jwt;

import com.study.security.exception.JwtAuthenticationException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationManager implements ReactiveAuthenticationManager {

    private final JwtService jwtService;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Mono.just(authentication)
                .cast(JwtToken.class)
                .filter(jwtToken -> jwtService.isTokenValid(jwtToken.getToken()))
                .map(JwtToken::withAuthenticated)
                .switchIfEmpty(Mono.error(new JwtAuthenticationException("쿠키에 인증 정보 포함되지 않아 로그인에 실패했습니다.")));
    }
}
