package com.study.security.entity;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Getter
@Builder
@Table("USER")
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserEntity {

    @Id
    private Long id;

    private String username;

    private String password;

    private String roleName;

    @CreatedDate
    private LocalDateTime createDt;

    @LastModifiedDate
    private LocalDateTime updateDt;
}
