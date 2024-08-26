package com.study.security.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Component("userDetails")
public class CustomUserDetails implements UserDetails {

    private String username;
    private String password;
    private String role;

    @JsonIgnore
    private Collection<? extends GrantedAuthority> authorities;

    private boolean isAccountNonExpired;

    private boolean isAccountNonLocked;

    private boolean isCredentialsNonExpired;

    private boolean isEnabled;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> listRole = new ArrayList<>();

        listRole.add(new SimpleGrantedAuthority(this.role));
        return listRole;
    }

    @Override
    public String getUsername() {
        return this.username;
    }
}