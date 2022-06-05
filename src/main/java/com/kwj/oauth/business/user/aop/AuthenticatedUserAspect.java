package com.kwj.oauth.business.user.aop;

import com.kwj.oauth.business.security.model.OAuthUserPrincipal;
import com.kwj.oauth.business.user.domain.SocialUser;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;

@Configuration
@EnableAspectJAutoProxy
@Aspect
public class AuthenticatedUserAspect {

    @Around("execution(* *(.., @com.example.oauth.aop.AuthenticatedUser (*), ..))")
    public Object extractSocialUser(ProceedingJoinPoint joinPoint) throws Throwable {
        OAuthUserPrincipal userPrincipal = (OAuthUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        SocialUser socialUser = userPrincipal.getSocialUser();

        Object[] args = Arrays.stream(joinPoint.getArgs())
                .map(data -> {
                    if (data instanceof SocialUser) {
                        data = socialUser;
                    }
                    return data;
                }).toArray();
        return joinPoint.proceed(args);
    }

}