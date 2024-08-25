package com.study.security.service;

import com.study.security.dto.UserRequestDTO;
import com.study.security.dto.UserResponseDTO;
import com.study.security.entity.UserEntity;
import com.study.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

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
}
