package com.example.oauth.config;

import com.example.oauth.model.UserRole;
import com.example.oauth.service.SocialUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Collections;

@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private final SocialUserService userService;

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
//        http.sessionManagement()
//                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // 경로 인가 관리
        http.authorizeRequests()
                .antMatchers("/", "/login", "/oauth1/**", "/oauth2/**", "/login/oauth1/**").permitAll()
                .antMatchers("/console/**").permitAll()
                .antMatchers("/admin/**").hasRole(UserRole.ADMIN.name())
                .anyRequest().authenticated();

        // OAuth 로그인 관리
        http.oauth2Login()
                .userInfoEndpoint()
                .userService(userService)
                .and()
                .loginPage("/login")
                .and()
                .logout()
                .logoutSuccessUrl("/");

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

}
