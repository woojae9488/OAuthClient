package com.kwj.oauth.business.security.model;

public enum UserRole {
    USER,
    ADMIN,
    ;

    public String getRoleType() {
        return "ROLE_" + this.name();
    }
}