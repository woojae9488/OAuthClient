package com.example.oauth.controller;

import com.example.oauth.aop.AuthenticatedUser;
import com.example.oauth.repository.model.SocialUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/api/user")
public class UserApiController {

    @ResponseBody
    @GetMapping
    public SocialUser getSocialUser(@AuthenticatedUser SocialUser socialUser) {
        return socialUser;
    }

}
