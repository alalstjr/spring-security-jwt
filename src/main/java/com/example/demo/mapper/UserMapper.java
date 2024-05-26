package com.example.demo.mapper;

import com.example.demo.dto.ProfileResponseDto;
import com.example.demo.dto.UserInfoRequestDto;
import com.example.demo.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(config = MapstructConfig.class)
public interface UserMapper {

    // 매퍼 팩토리(종속성 주입 없이 사용하도록 설정)
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserEntity dtoToEntity(UserInfoRequestDto dto);

    ProfileResponseDto entityToDto(UserEntity dto);
}