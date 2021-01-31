package com.example.oauth.model;

public enum UserRole {
    USER,
    ADMIN;

    public String getRoleType() {
        return "ROLE_" + this.name();
    }
}