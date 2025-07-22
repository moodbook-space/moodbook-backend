package org.com.moodbook.security.config;

import lombok.RequiredArgsConstructor;
import org.com.moodbook.security.jwt.JwtTokenFilter;
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

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtTokenFilter jwtTokenFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/ws-chat/**") // ⭐️ SockJS/WebSocket CSRF 예외처리!
                .disable()
            )
            .authorizeHttpRequests(auth -> auth
                // 아래처럼 ws-chat 엔드포인트는 인증 예외처리(permitAll) ★
                .requestMatchers("/ws-chat/**").permitAll()
                // 나머지는 기존 코드와 동일
                .requestMatchers("/api/oauth/**").permitAll()
                .requestMatchers("/api/notification/**").permitAll()
                .requestMatchers(
                    "/redis/test",
                    "/auth/**",
                    "/api/oauth/",
                    "/admin/**",
                    "/api/admin/**",
                    "/api/books/**",
                    "/api/recent-books/**",
                    "/api/reviews/**",
                    "/api/openai/**",
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/chat.html",
                    "/main.html",
                    "/images/**",
                    "/sounds/**",
                    "/favicon.ico",
                    "/error",
                    "/emotion/**",
                    "/api/emotion/**",
                    "/api/books/**",
                    "/actuator/prometheus",     //프로메테우스
                    "/login",
                    "/signUp",
                    "/books/**",
                    "/api/books/**"
                ).permitAll()
                .requestMatchers("/api/chat-rooms/**").authenticated()
                .requestMatchers("/chat-rooms/**").authenticated()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
        throws Exception {
        return config.getAuthenticationManager();
    }
}
