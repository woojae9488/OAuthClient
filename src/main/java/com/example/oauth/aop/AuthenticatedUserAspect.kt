package com.example.oauth.aop

import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.aspectj.lang.annotation.Around
import kotlin.Throws
import org.aspectj.lang.ProceedingJoinPoint
import com.example.oauth.model.oauth.OAuthUserPrincipal
import org.springframework.security.core.context.SecurityContextHolder
import com.example.oauth.repository.model.SocialUser
import org.aspectj.lang.annotation.Aspect
import org.springframework.context.annotation.Configuration
import java.util.*

@Configuration
@EnableAspectJAutoProxy
@Aspect
class AuthenticatedUserAspect {

    @Around("execution(* *(.., @com.example.oauth.aop.AuthenticatedUser (*), ..))")
    @Throws(Throwable::class)
    fun extractSocialUser(joinPoint: ProceedingJoinPoint): Any {
        val userPrincipal = SecurityContextHolder.getContext().authentication.principal as OAuthUserPrincipal
        val socialUser: SocialUser = userPrincipal.socialUser
        val args = joinPoint.args.map {
            return if (it is SocialUser) socialUser else it
        }
        return joinPoint.proceed(args.toTypedArray())
    }

}