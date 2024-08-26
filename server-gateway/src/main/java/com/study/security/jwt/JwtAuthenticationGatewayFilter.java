package com.study.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.security.config.RedissTemplate;
import com.study.security.dto.CustomUserDetails;
import com.study.security.dto.ErrorResponseDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Component
public class JwtAuthenticationGatewayFilter extends
        AbstractGatewayFilterFactory<JwtAuthenticationGatewayFilter.Config> {

    private final JwtService jwtService;
    private final ObjectMapper objectMapper;
    private final RedissTemplate redissTemplate;

    public JwtAuthenticationGatewayFilter(JwtService jwtService, ObjectMapper objectMapper, RedissTemplate redissTemplate) {
        super(Config.class);
        this.jwtService = jwtService;
        this.objectMapper = objectMapper;
        this.redissTemplate = redissTemplate;
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return Collections.singletonList(AuthKeyWord.ROLE_KEY.value);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            if (!containsAuthorization(request)) {
                return onError(exchange, "쿠키에 인증 정보 포함되지 않아 로그인에 실패했습니다.", HttpStatus.BAD_REQUEST);
            }

            String accessToken = extractToken(request, AuthKeyWord.ACCESS_TOKEN.value);
            String refreshToken = extractToken(request, AuthKeyWord.REFRESH_TOKEN.value);

            // 만료일 유효성 검사
            try {
                String username = jwtService.extractUsername(refreshToken);

                // 기존 redis의 refresh 토큰 정보 조회
                JwtInfo jwtInfoRedis = objectMapper.convertValue(redissTemplate.get(username, username), JwtInfo.class);

                // redis 에 토큰값 존재 여부 체크
                if (ObjectUtils.isEmpty(jwtInfoRedis)) {
                    return onError(exchange, "로그인 상태가 유효하지 않거나 일정 시간 동안 활동이 없어 자동 로그아웃 되었습니다.", HttpStatus.UNAUTHORIZED);
                }

                if (!jwtService.isTokenValid(accessToken)) {// access 토큰이 유효하지 않을 때

                    // accessToken 만료 시 refreshToken 조회 후 검증 후 재발급 / error
                    if (jwtService.isTokenValid(refreshToken)) {
                        // access 토큰 재발급 후 재발급 한 access 토큰 redis 저장
                        accessToken = regenerateTokenAndSaveRedis(jwtInfoRedis, username, refreshToken);
                    } else {
                        // refreshToken 만료 시 쿠키 지우기
                        clearTokenCookie(response);
                        return onError(exchange, "로그인 상태가 유효하지 않거나 일정 시간 동안 활동이 없어 자동 로그아웃 되었습니다.", HttpStatus.UNAUTHORIZED);
                    }

                }
            } catch (JwtException e) {
                // refreshToken 만료 시 쿠키 지우기
                clearTokenCookie(response);
                return onError(exchange, "로그인 상태가 유효하지 않거나 일정 시간 동안 활동이 없어 자동 로그아웃 되었습니다.", HttpStatus.UNAUTHORIZED);
            }

            // 권한 유효성 검사
            if (!hasRole(jwtService.extractRoles(accessToken), config.roles)) {
                return onError(exchange, "해당 페이지 또는 기능에 접근 권한이 없습니다.", HttpStatus.UNAUTHORIZED);
            }

            // 재발급 한 access 토큰 쿠키로 전달
            response.addCookie(createAccessTokenCookie(accessToken));

            return chain.filter(exchange.mutate().request(request).build());
        };
    }

    /**
     * 쿠키 삭제
     */
    private void clearTokenCookie(ServerHttpResponse response) {
        response.addCookie(ResponseCookie.from(AuthKeyWord.ACCESS_TOKEN.value, "")
                .path("/")
                .httpOnly(true)
                .secure(true)
                .maxAge(0)
                .build());
        response.addCookie(ResponseCookie.from(AuthKeyWord.REFRESH_TOKEN.value, "")
                .path("/")
                .httpOnly(true)
                .secure(true)
                .maxAge(0)
                .build());
    }

    /**
     * access 토큰 쿠키 생성
     */
    private ResponseCookie createAccessTokenCookie(String accessToken) {
        return ResponseCookie.from(AuthKeyWord.ACCESS_TOKEN.value, accessToken)
                .path("/")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .build();
    }

    /**
     * access 토큰 재발급 후 재발급 한 access 토큰 redis 저장
     */
    private String regenerateTokenAndSaveRedis(JwtInfo jwtInfoRedis, String username, String refreshToken) {

        // access 토큰 재발급
        JwtInfo jwtInfo = jwtService.reGenerateToken(Map.of(), this.getUserDetailsByRefreshToken(refreshToken));

        // 기존 refresh 토큰 정보 set
        jwtInfo.setRefreshToken(jwtInfoRedis.getRefreshToken());
        jwtInfo.setRefreshTokenExpiredAt(jwtInfoRedis.getRefreshTokenExpiredAt());

        // 재발급 한 access 토큰 redis 저장
        jwtService.saveRedisToken(username, username, jwtInfo);

        return jwtInfo.getToken();
    }

    /**
     * 토큰 재발급(동기)
     */
    private UserDetails getUserDetailsByRefreshToken(String refreshToken) {
        Claims claims = jwtService.extractAllClaims(refreshToken);
        String roles = claims.get(AuthKeyWord.ROLE_KEY.value).toString();

        return CustomUserDetails.builder()
                .role(roles.substring(1, roles.length() - 1))
                .username(claims.getSubject())
                .build();
    }

    private boolean containsAuthorization(ServerHttpRequest request) {
        return request.getCookies().containsKey(AuthKeyWord.ACCESS_TOKEN.value);
    }

    private String extractToken(ServerHttpRequest request, String name) {
        if (ObjectUtils.isEmpty(request.getCookies().getFirst(name))) {
            return "";
        } else {
            return Objects.requireNonNull(request.getCookies().getFirst(name)).getValue();
        }
    }

    private boolean hasRole(List<String> userRole, String role) {
        List<String> roleList = Arrays.stream(role.split(", ")).toList();

        return roleList.stream().anyMatch(userRole::contains);
    }

    private Mono<Void> onError(
            ServerWebExchange exchange, String message, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        ObjectMapper mapper = new ObjectMapper();
        byte[] bytes;
        try {
            bytes = mapper.writeValueAsBytes(
                    ErrorResponseDTO.builder()
                            .error(status.name())
                            .message(message)
                            .path(exchange.getRequest().getURI().getPath())
                            .requestId(UUID.randomUUID().toString())
                            .status(status.value())
                            .timestamp(ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                            .build()
            );
        } catch (Exception e) {
            bytes = new byte[0];
        }

        DataBuffer buffer = response.bufferFactory().wrap(bytes);
        return response.writeWith(Mono.just(buffer));
    }

    @Setter
    public static class Config {
        private String roles;

    }

    @Override
    public Class<Config> getConfigClass() {
        return Config.class;
    }
}