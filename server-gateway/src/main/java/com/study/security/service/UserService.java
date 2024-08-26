package com.study.security.service;

import com.study.security.config.RedisReactiveTemplate;
import com.study.security.dto.CustomUserDetails;
import com.study.security.dto.LoginRequestDto;
import com.study.security.dto.UserRequestDTO;
import com.study.security.dto.UserResponseDTO;
import com.study.security.entity.UserEntity;
import com.study.security.jwt.JwtService;
import com.study.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public Mono<UserResponseDTO> join(Mono<UserRequestDTO> userRequestDTOMono) {
        return userRequestDTOMono.flatMap(this::createUser);
    }

    public Mono<UserResponseDTO> createUser(UserRequestDTO dto) {
        dto.setPassword(this.passwordEncoder.encode(dto.getPassword()));
        return this.userRepository.save(
                        UserEntity.builder()
                                .username(dto.getUsername())
                                .password(dto.getPassword())
                                .roleName(dto.getRoleName())
                                .build()
                )
                .flatMap(entity -> Mono.just(UserResponseDTO.builder()
                        .username(entity.getUsername())
                        .roleName(entity.getRoleName())
                        .build()
                ))
                .onErrorResume(error -> {
                    log.error("Create User Error {}", error.getMessage());
                    return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Create User Error"));
                })
                ;
    }

    public Mono<UserResponseDTO> login(Mono<LoginRequestDto> dto, ServerHttpResponse response) {
        return dto.flatMap(
                loginRequestDto -> {
                    String username = loginRequestDto.getUsername();

                    return checkLoginUserExists(username).thenReturn(username)
                            .flatMap(userInfo -> passwordMatches(loginRequestDto))
                            .flatMap(userDetails -> this.jwtService.generateTokens(userDetails, response))
                            ;
                }
        );
    }

    private Mono<Void> checkLoginUserExists(String username) {
        return checkIfUsernameExists(username)
                .flatMap(usernameExists -> {
                    if (Boolean.TRUE.equals(usernameExists)) return Mono.empty();
                    return Mono.error(
                            new ResponseStatusException(HttpStatus.BAD_REQUEST, "아이디 또는 비밀번호를 확인해 주세요.")
                    );
                })
                ;
    }

    private Mono<Boolean> checkIfUsernameExists(String username) {
        return this.findByUsername(username)
                .flatMap(user -> Mono.just(Boolean.TRUE)) // username 이 이미 존재하는 경우 접근 성공
                .defaultIfEmpty(false); // username 이 존재하지 않는 경우 로그인 실패
    }

    private Mono<UserDetails> passwordMatches(LoginRequestDto loginRequestDto) {
        return this.findByUsername(loginRequestDto.getUsername())
                .flatMap(userDetails -> {
                    if (this.passwordEncoder.matches(loginRequestDto.getPassword(), userDetails.getPassword())) {
                        return Mono.just(userDetails);
                    } else {
                        // 로그인 실패 이력 기록
                        return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "아이디 또는 비밀번호를 확인해 주세요."));
                    }
                });
    }

    public Mono<UserDetails> findByUsername(String username) {
        // 사용자 아이디로 userEntity 조회
        Mono<UserEntity> userEntityMono = this.userRepository.findByUsername(username);

        return userEntityMono
                .flatMap(userEntity -> {
                    // userEntity 가 비어 있는 경우 Mono.empty() 반환
                    if (ObjectUtils.isEmpty(userEntity)) {
                        return Mono.empty();
                    }

                    return Mono.just(CustomUserDetails.builder()
                            .username(userEntity.getUsername())
                            .password(userEntity.getPassword())
                            .role(userEntity.getRoleName())
                            .build());
                });
    }
}
