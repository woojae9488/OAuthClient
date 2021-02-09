package com.example.oauth.model

enum class UserRole {
    USER, ADMIN;

    val roleType = "ROLE_$name"
}