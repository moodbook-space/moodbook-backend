package org.com.moodbook.security.config;

import lombok.RequiredArgsConstructor;
import org.com.moodbook.security.jwt.JwtTokenFilter;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration//설정 클래스 등록
@EnableWebSecurity//스프링 시큐리티 활성화
@RequiredArgsConstructor//생성자 자동 생성
public class SecurityConfig {
    private final JwtTokenFilter jwtTokenFilter;

    //회원가입시에 비밀번호를 암호화해주는 메서드
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable()) // csrf 비활성화
            .authorizeHttpRequests(auth -> auth

                .requestMatchers("/api/oauth/**").permitAll()
                .requestMatchers("/api/chat-rooms/**").permitAll()
                .requestMatchers("/chat-rooms/**").permitAll()
                .requestMatchers("/api/notification/**").permitAll()
                .requestMatchers(
                    "/api/oauth/",
                    "/admin/",
                    "/api/books/**",
                    "/api/recent-books/**",
                    "/api/reviews/**",
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/chat.html",
                    "/main.html",
                    "/images/",
                    "/sounds/",
                    "/favicon.ico",
                    "/error",
                    "/emotion/**",
                    "/api/emotion/**",
                    "/api/books/**",
                    "/login",
                    "signup"
                ).permitAll()

                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)//filter등록
            .build();
    }

    // @Authenticational null 문제 처리
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
        throws Exception {
        return config.getAuthenticationManager();
    }

}