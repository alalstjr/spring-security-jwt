package com.study.security.controller;

import com.study.security.dto.UserRequestDTO;
import com.study.security.dto.UserResponseDTO;
import com.study.security.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class MemberController {

    private final UserService userService;

    @PostMapping("/join")
    public Mono<UserResponseDTO> join(
            @RequestBody
            Mono<UserRequestDTO> userRequestDTO
    ) {
        return userService.join(userRequestDTO);
    }
}