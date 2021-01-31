package com.example.oauth.controller;


import com.example.oauth.model.OAuth1Requirement;
import com.example.oauth.service.SocialLoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.social.oauth1.OAuthToken;
import org.springframework.social.twitter.api.impl.TwitterTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class AuthenticationController {
    private final SocialLoginService socialLoginService;

    @GetMapping("/oauth1/authorization/twitter")
    public String oauthTwitter() {
        return "redirect:/login/oauth1/twitter";
    }

    @GetMapping("/login/oauth1/twitter")
    public void twitterLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        OAuth1Requirement oauth1Requirement = socialLoginService.getTwitterOAuthRequirement();
        request.getServletContext().setAttribute("token", oauth1Requirement.getRequestToken());
        response.sendRedirect(oauth1Requirement.getAuthenticationUri());
    }

    @ResponseBody
    @GetMapping("/login/oauth1/twitter/callback")
    public Object twitterLoginCallback(HttpServletRequest request, @RequestParam(name = "oauth_verifier") String oauthVerifier) {
        OAuthToken requestToken = (OAuthToken) request.getServletContext().getAttribute("token");
        request.getServletContext().removeAttribute("token");

        TwitterTemplate twitterTemplate = socialLoginService.getTwitterTemplate(requestToken, oauthVerifier);

//        socialLoginService.setAuthentication(map);
//        socialLoginService.saveUserIfNotExist(connection, map);
        return socialLoginService.getUserInformationMap(twitterTemplate);
    }


}

