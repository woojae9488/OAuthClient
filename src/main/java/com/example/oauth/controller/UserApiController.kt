package com.example.oauth.controller

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.GetMapping
import com.example.oauth.aop.AuthenticatedUser
import com.example.oauth.repository.model.SocialUser
import org.springframework.stereotype.Controller

@Controller
@RequestMapping("/api/user")
class UserApiController {

    @ResponseBody
    @GetMapping
    fun getSocialUser(@AuthenticatedUser socialUser: SocialUser) = socialUser

}