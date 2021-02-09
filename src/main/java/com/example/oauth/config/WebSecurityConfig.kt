package com.example.oauth.config

import com.example.oauth.filter.AuthenticationSuccessHandler
import com.example.oauth.filter.TokenAuthenticationFilter
import com.example.oauth.filter.TwitterCallbackAuthenticationFilter
import com.example.oauth.filter.TwitterLoginProcessingFilter
import com.example.oauth.model.UserRole
import com.example.oauth.service.OAuth2UserService
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.authentication.HttpStatusEntryPoint
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@EnableWebSecurity
class WebSecurityConfig(
    private val twitterLoginProcessingFilter: TwitterLoginProcessingFilter,
    private val twitterCallbackAuthenticationFilter: TwitterCallbackAuthenticationFilter,
    private val authenticationSuccessHandler: AuthenticationSuccessHandler,
    private val tokenAuthenticationFilter: TokenAuthenticationFilter,
    private val userService: OAuth2UserService,
) : WebSecurityConfigurerAdapter() {

    override fun configure(web: WebSecurity) {
        web.ignoring().antMatchers("/webjars/**")
    }

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        // 기본적인 보안 설정
        http.cors()
            .and()
            .csrf()
            .disable()

        // 세션 관리
        http.sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

        // 경로 인가 관리
        http.authorizeRequests()
            .antMatchers("/", "/login", "/oauth1/**", "/oauth2/**", "/login/oauth1/**").permitAll()
            .antMatchers("/console/**").permitAll()
            .antMatchers("/admin/**").hasRole(UserRole.ADMIN.name)
            .anyRequest().authenticated()

        // OAuth2 로그인 관리
        http.oauth2Login()
            .userInfoEndpoint()
            .userService(userService)
            .and()
            .loginPage("/login")
            .successHandler(authenticationSuccessHandler)
            .and()
            .logout()
            .logoutSuccessUrl("/")

        // OAuth1 및 토큰 로그인 관리
        twitterCallbackAuthenticationFilter.setAuthenticationSuccessHandler(authenticationSuccessHandler)
        http.addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
        http.addFilterBefore(twitterLoginProcessingFilter, UsernamePasswordAuthenticationFilter::class.java)
        http.addFilterBefore(twitterCallbackAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)

        // 예외 핸들링
        http.exceptionHandling()
            .authenticationEntryPoint(HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))

        // H2를 위한 임시 설정
        http.headers()
            .frameOptions()
            .sameOrigin()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration().apply {
            allowedOrigins = listOf("*")
            allowedMethods = listOf("*")
            allowedHeaders = listOf("*")
        }
        return UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration("/**", configuration)
        }
    }

    @Bean
    @Throws(Exception::class)
    override fun authenticationManagerBean(): AuthenticationManager {
        return super.authenticationManagerBean()
    }
}