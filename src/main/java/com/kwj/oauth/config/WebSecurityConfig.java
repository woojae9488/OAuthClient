package com.kwj.oauth.config;

import com.kwj.oauth.business.security.application.OAuth2UserService;
import com.kwj.oauth.business.security.filter.TokenAuthenticationFilter;
import com.kwj.oauth.business.security.filter.TwitterCallbackAuthenticationFilter;
import com.kwj.oauth.business.security.filter.TwitterLoginProcessingFilter;
import com.kwj.oauth.business.security.handler.AuthenticationSuccessHandler;
import com.kwj.oauth.business.security.model.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Collections;

@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final TwitterLoginProcessingFilter twitterLoginProcessingFilter;
    private final TwitterCallbackAuthenticationFilter twitterCallbackAuthenticationFilter;
    private final AuthenticationSuccessHandler authenticationSuccessHandler;
    private final TokenAuthenticationFilter tokenAuthenticationFilter;
    private final OAuth2UserService userService;

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/webjars/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 기본적인 보안 설정
        http.cors()
                .and()
                .csrf()
                .disable();

        // 세션 관리
        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // 경로 인가 관리
        http.authorizeRequests()
                .antMatchers("/", "/login", "/oauth1/**", "/oauth2/**", "/login/oauth1/**").permitAll()
                .antMatchers("/console/**").permitAll()
                .antMatchers("/admin/**").hasRole(UserRole.ADMIN.name())
                .anyRequest().authenticated();

        // OAuth2 로그인 관리
        http.oauth2Login()
                .userInfoEndpoint()
                .userService(userService)
                .and()
                .loginPage("/login")
                .successHandler(authenticationSuccessHandler)
                .and()
                .logout()
                .logoutSuccessUrl("/");

        // OAuth1 및 토큰 로그인 관리
        twitterCallbackAuthenticationFilter.setAuthenticationSuccessHandler(authenticationSuccessHandler);
        http.addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(twitterLoginProcessingFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(twitterCallbackAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        // 예외 핸들링
        http.exceptionHandling()
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));

        // H2를 위한 임시 설정
        http.headers()
                .frameOptions()
                .sameOrigin();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Collections.singletonList("*"));
        configuration.setAllowedMethods(Collections.singletonList("*"));
        configuration.setAllowedHeaders(Collections.singletonList("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

}
