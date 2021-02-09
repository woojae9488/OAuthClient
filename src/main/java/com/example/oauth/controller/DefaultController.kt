package com.example.oauth.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class DefaultController {

    @GetMapping("/")
    fun root() = "redirect:/login"

    @GetMapping("/login")
    fun login() = "login.html"

}