package com.study.security.jwt;

import com.study.security.config.RedisReactiveTemplate;
import com.study.security.config.RedissTemplate;
import com.study.security.dto.UserResponseDTO;
import com.study.security.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {
    private final UserRepository userRepository;
    private final RedissTemplate redissTemplate;
    private final RedisReactiveTemplate redisReactiveTemplate;

    @Value("${jwt.secret.key}")
    private String secretKey;

    @Value("${jwt.access.expiration_time}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh.expiration_time}")
    private long refreshTokenExpiration;

    public String extractUsername(String jwt) {
        return extractClaim(jwt, Claims::getSubject);
    }

    public List<String> extractRoles(String jwt) {
        return extractClaim(jwt, claims -> {
            Object rolesObject = claims.get(AuthKeyWord.ROLE_KEY.value);
            List<String> rolesStringList = new ArrayList<>();

            if (rolesObject instanceof List<?> rolesList) {
                for (Object role : rolesList) {
                    if (role instanceof String stringRole) {
                        rolesStringList = Arrays.stream(stringRole.split(", ")).toList();
                    }
                }
            }

            return rolesStringList;
        });
    }

    public Mono<UserResponseDTO> generateTokens(UserDetails userDetails, ServerHttpResponse response) {
        String username = userDetails.getUsername();

        // 토큰 발급
        JwtInfo jwtInfo = generateToken(Map.of(), userDetails);

        // 쿠키 생성
        List<ResponseCookie> authCookie = createAuthCookies(jwtInfo);

        for (ResponseCookie cookie : authCookie) {
            response.addCookie(cookie);
        }

        return this.saveRedisTokenReactive(username, username, jwtInfo)
                .thenReturn(UserResponseDTO.builder()
                        .roleName(userDetails.getAuthorities().stream()
                                .map(GrantedAuthority::getAuthority)
                                .map(role -> role.substring("ROLE_".length()))
                                .collect(Collectors.joining())
                        )
                        .username(username)
                        .authCookie(authCookie)
                        .build());
    }

    /**
     * 쿠키 생성
     */
    private List<ResponseCookie> createAuthCookies(JwtInfo jwtInfo) {
        List<ResponseCookie> authCookie = new ArrayList<>();
        authCookie.add(
                ResponseCookie.from(AuthKeyWord.ACCESS_TOKEN.value, jwtInfo.getToken())
                        .httpOnly(true)
                        .secure(true) // true in production
                        .sameSite("None")
                        .path("/")
                        .build()
        );
        authCookie.add(
                ResponseCookie.from(AuthKeyWord.REFRESH_TOKEN.value, jwtInfo.getRefreshToken())
                        .httpOnly(true)
                        .secure(true)
                        .sameSite("None")
                        .path("/")
                        .build()
        );
        return authCookie;
    }

    /**
     * 토큰 정보 redis에 저장
     */
    public Mono<Boolean> saveRedisTokenReactive(String key, String hashKey, Object value) {
        return redisReactiveTemplate.set(key, hashKey, value)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Redis Token 저장에 실패했습니다.")));
    }

    // 토큰 정보 redis에 저장
    public void saveRedisToken(String key, String hashKey, Object value) {
        redissTemplate.set(key, hashKey, value);
    }

    public JwtInfo generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        long currentTimeMillis = System.currentTimeMillis();

        Date accessTokenExpired = new Date(currentTimeMillis + accessTokenExpiration * 1000);
        Date refreshTokenExpired = new Date(currentTimeMillis + refreshTokenExpiration * 1000);

        String username = userDetails.getUsername();

        // ACCESS TOKEN 발급
        String accessToken = Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(username)
                .claim(AuthKeyWord.ROLE_KEY.value, userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toArray()
                )
                .setIssuer(String.valueOf(new Date(currentTimeMillis)))
                .setExpiration(accessTokenExpired)
                .signWith(getSigningKey())
                .compact();

        // REFRESH TOKEN 발급
        String refreshToken = Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(username)
                .claim(AuthKeyWord.ROLE_KEY.value, userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toArray()
                )
                .setIssuer(String.valueOf(new Date(currentTimeMillis)))
                .setExpiration(refreshTokenExpired)
                .signWith(getSigningKey())
                .compact();


        return JwtInfo.builder()
                .token(accessToken)
                .tokenExpiredAt(String.valueOf(accessTokenExpired))
                .refreshToken(refreshToken)
                .refreshTokenExpiredAt(String.valueOf(refreshTokenExpired))
                .build();
    }

    public JwtInfo reGenerateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        long currentTimeMillis = System.currentTimeMillis();

        Date accessTokenExpired = new Date(currentTimeMillis + accessTokenExpiration * 1000);

        // ACCESS TOKEN 발급
        String accessToken = Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .claim(AuthKeyWord.ROLE_KEY.value, userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toArray()
                )
                .setIssuer(String.valueOf(new Date(currentTimeMillis)))
                .setExpiration(accessTokenExpired)
                .signWith(getSigningKey())
                .compact();


        return JwtInfo.builder()
                .token(accessToken)
                .tokenExpiredAt(String.valueOf(accessTokenExpired))
                .build();
    }

    private SecretKey getSigningKey() {
        byte[] bytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(bytes);
    }

    // 토큰 유효성 검사
    public boolean isTokenValid(String accessToken) {
        try {
            return !isTokenExpired(accessToken);
        } catch (JwtException e) {
            log.error(e.getMessage());
            return false;
        }
    }

    // 토큰 만료 시간 유효성 검사
    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    // 토큰 유효성 검사
    // Claims 추출
    private <T> T extractClaim(String jwt, Function<Claims, T> claimResolver) {
        Claims claims = extractAllClaims(jwt);
        return claimResolver.apply(claims);
    }

    // 토큰 유효성 검사
    // Claims 추출
    public Claims extractAllClaims(String jwt) throws JwtException {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(jwt)
                .getBody();
    }
}
