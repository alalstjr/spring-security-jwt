package com.example.demo.service;

import com.example.demo.constant.AuthenticationList;
import com.example.demo.dto.ProfileResponseDto;
import com.example.demo.dto.UserInfoRequestDto;
import com.example.demo.entity.UserEntity;
import com.example.demo.mapper.UserMapper;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public ProfileResponseDto join(UserInfoRequestDto dto) {
        dto.setPassword(this.passwordEncoder.encode(dto.getPassword()));
        dto.setRole(AuthenticationList.ROLE_MEMBER.roll());

        return UserMapper.INSTANCE.entityToDto(
                userRepository.save(UserMapper.INSTANCE.dtoToEntity(dto))
        );
    }
}
