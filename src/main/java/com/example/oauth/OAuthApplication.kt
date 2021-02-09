package com.example.oauth

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class OAuthApplication

fun main(args: Array<String>) {
    SpringApplication.run(OAuthApplication::class.java, *args)
}
