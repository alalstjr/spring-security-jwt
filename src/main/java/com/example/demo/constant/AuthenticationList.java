package com.example.demo.constant;

public enum AuthenticationList {

    ROLE_ADMIN("ROLE_ADMIN", "ADMIN"),
    ROLE_MEMBER("ROLE_USER", "USER")
    ;

    public final String roll;
    public final String auth;

    AuthenticationList(String roll, String auth) {
        this.roll = roll;
        this.auth = auth;
    }

    public String roll(){return roll;}
    public String auth(){return auth;}
}
