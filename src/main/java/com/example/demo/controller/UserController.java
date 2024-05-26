package com.example.demo.controller;

import com.example.demo.dto.ProfileResponseDto;
import com.example.demo.dto.UserInfoRequestDto;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/join")
    ResponseEntity<ProfileResponseDto> join(UserInfoRequestDto authentication) {
        return new ResponseEntity<>(this.userService.join(authentication), HttpStatus.CREATED);
    }
}