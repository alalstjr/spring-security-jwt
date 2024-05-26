package com.example.demo.mapper;

import com.example.demo.dto.ProfileResponseDto;
import com.example.demo.dto.UserInfoRequestDto;
import com.example.demo.entity.UserEntity;
import java.util.Set;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-05-26T21:58:34+0900",
    comments = "version: 1.6.0.Beta1, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.7.jar, environment: Java 17.0.8.1 (Eclipse Adoptium)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserEntity dtoToEntity(UserInfoRequestDto dto) {
        if ( dto == null ) {
            return null;
        }

        UserEntity.UserEntityBuilder userEntity = UserEntity.builder();

        userEntity.username( dto.getUsername() );
        userEntity.password( dto.getPassword() );

        return userEntity.build();
    }

    @Override
    public ProfileResponseDto entityToDto(UserEntity dto) {
        if ( dto == null ) {
            return null;
        }

        String username = null;

        username = dto.getUsername();

        Set<String> roles = null;

        ProfileResponseDto profileResponseDto = new ProfileResponseDto( username, roles );

        return profileResponseDto;
    }
}
