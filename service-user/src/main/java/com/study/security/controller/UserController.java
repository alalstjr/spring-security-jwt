package com.study.security.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @GetMapping
    public ResponseEntity<HttpStatus> get() {
        log.info("API OK");
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
