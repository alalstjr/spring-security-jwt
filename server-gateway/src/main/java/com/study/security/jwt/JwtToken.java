package com.study.security.jwt;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
public class JwtToken extends AbstractAuthenticationToken {

    private final String token;
    private final UserDetails principal;

    JwtToken(String token, UserDetails principal) {
        super(principal.getAuthorities());
        this.token = token;
        this.principal = principal;
    }

    Authentication withAuthenticated() {
        JwtToken cloned = new JwtToken(token, principal);
        cloned.setAuthenticated(true);
        return cloned;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof JwtToken test))
            return false;
        if (this.getToken() == null && test.getToken() != null)
            return false;
        if (this.getToken() != null && !this.getToken().equals(test.getToken()))
            return false;
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        int code = super.hashCode();
        if (this.getToken() != null) {
            code ^= this.getToken().hashCode();
        }
        return code;
    }
}
